/*
 * sbt-haxe
 * Copyright 2014 深圳岂凡网络有限公司 (Shenzhen QiFun Network Corp., LTD)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.microbuilder.sbtHaxe

import java.io.{PipedOutputStream, PipedInputStream}

import com.thoughtworks.microbuilder.sbtHaxe.DependencyVersion.{GitVersion, LastVersion, SpecificVersion}
import sbt._
import Keys._
import sbt.plugins.JvmPlugin

import scala.io.Codec
import scala.util.parsing.json.{JSONArray, JSONObject}

/**
 * A plugin that provides the common settings for HaxeJavaPlugin and HaxeCSharpPlugin etc.
 *
 */
final object BaseHaxePlugin extends AutoPlugin {
  override final def requires = JvmPlugin

  override final def trigger = allRequirements

  final object autoImport extends HaxeKeys with HaxeConfigurations {
    def haxelibOptions(dependencies: Map[String, DependencyVersion]): Seq[String] = {
      dependencies.flatMap {
        case (lib, LastVersion) =>
          Seq("-lib", lib)
        case (lib, GitVersion(_, _, _)) =>
          Seq("-lib", s"$lib:git")
        case (lib, SpecificVersion(version)) =>
          Seq("-lib", s"$lib:$version")
      }(collection.breakOut(Seq.canBuildFrom))
    }
  }

  import autoImport._

  private val WhichUserRegex = """Which of these users are you: \[(.*)\]""".r

  override final lazy val projectSettings: Seq[Setting[_]] =
    inConfig(Haxe)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestHaxe)(SbtHaxe.baseHaxeSettings) ++
      SbtHaxe.docSetting(Haxe, Compile) ++
      SbtHaxe.docSetting(TestHaxe, Test) ++
      Seq(
        haxelibInstallDependencies <<= Def.task {
          for ((lib, version) <- haxelibDependencies.value) {
            val logger = (streams in haxelibInstallDependencies).value.log
            val processHaxelibInstall = version match {
              case SpecificVersion(specificVersion) => {
                Seq(
                  haxelibCommand.value,
                  "install",
                  lib,
                  specificVersion,
                  "--always"
                )
              }
              case GitVersion(url, branch, path) => {
                Seq(
                  haxelibCommand.value,
                  "git",
                  lib,
                  url,
                  branch,
                  path,
                  "--always"
                )
              }
              case LastVersion => {
                Seq(
                  haxelibCommand.value,
                  "install",
                  lib,
                  "--always"
                )
              }
            }
            logger.info(processHaxelibInstall.mkString("\"", "\" \"", "\""))
            processHaxelibInstall !< logger match {
              case 0 =>
              case result =>
                throw new MessageOnlyException(
                  raw"""Unexpected return value $result for
  ${processHaxelibInstall.mkString("\"", "\" \"", "\"")}""")
            }
          }
        } tag SbtHaxe.HaxelibLock,
        publish in Haxe := {
          val logger = (streams in publish in Haxe).value.log
          val contributors = haxelibContributors.value
          val username = haxelibSubmitUsername.?.value match {
            case None => {
              contributors.headOption match {
                case None => throw new MessageOnlyException(s"haxelibContributors should not be empty.")
                case Some(firstContributor) => firstContributor
              }
            }
            case Some(username) => {
              if (contributors.contains(username)) {
                username
              } else {
                throw new MessageOnlyException(
                  s"haxelibSubmitUsername ($username) must be one of the haxelibContributors (${
                    contributors.mkString(" or ")
                  }).")
              }
            }
          }
          val commandWithoutPassword = Seq(
            haxelibCommand.value,
            "submit",
            (packageBin in Haxe).value.toString,
            haxelibSubmitUsername.?.value.getOrElse(haxelibContributors.value(0))
          )
          val processHaxelibSubmit = haxelibSubmitPassword.?.value match {
            case None => commandWithoutPassword
            case Some(password) => commandWithoutPassword :+ password
          }
          logger.info(processHaxelibSubmit.mkString("\"", "\" \"", "\""))
          processHaxelibSubmit !< logger match {
            case 0 =>
            case result =>
              throw new MessageOnlyException(
                raw"""Unexpected return value $result for
  ${processHaxelibSubmit.mkString("\"", "\" \"", "\"")}""")
          }
        },
        publishLocal in Haxe <<= Def.task {
          val logger = (streams in publishLocal in Haxe).value.log
          val processHaxelibLocal = Seq(
            haxelibCommand.value,
            "local",
            (packageBin in Haxe).value.toString,
            "--always"
          )
          logger.info(processHaxelibLocal.mkString("\"", "\" \"", "\""))
          processHaxelibLocal !< logger match {
            case 0 =>
            case result =>
              throw new MessageOnlyException(
                raw"""Unexpected return value $result for
  ${processHaxelibLocal.mkString("\"", "\" \"", "\"")}""")
          }
        } tag SbtHaxe.HaxelibLock,
        haxelibContributors := developers.value.map(_.id),
        haxelibJson := JSONObject(Map(
          Seq(
            name.?.value.map("name" -> _),
            homepage.value.map("url" -> _.toString),
            licenses.?.value.flatMap(_.unzip._1.headOption.map("license" -> _)),
            Some("tags" -> JSONArray(haxelibTags.value.toList)),
            description.?.value.map("description" -> _),
            version.?.value.map("version" -> _),
            haxelibReleaseNote.?.value.map("releasenote" -> _),
            Some("contributors" -> JSONArray(haxelibContributors.value.toList)),
            Some("dependencies" -> JSONObject(haxelibDependencies.value.mapValues {
              case LastVersion => ""
              case GitVersion(_, _, _) => "git" // FIXME: Should follow the format in discussion of https://github.com/HaxeFoundation/haxelib/issues/238
              case SpecificVersion(v) => v
            }))
          ).flatten: _*
        )),
        makeHaxelibJson := {
          val content = haxelibJson.value.toString()
          val file = (sourceManaged in Haxe).value / "haxelib.json"
          if (!file.exists || content != IO.read(file, scala.io.Codec.UTF8.charSet)) {
            IO.write(file, content, scala.io.Codec.UTF8.charSet)
          }
          file
        },
        makeExtraParamsHxml := {
          val parameters = haxeExtraParams.value
          if (parameters.isEmpty) {
            None
          } else {
            val content = haxeExtraParams.value.mkString("\n")
            val file = (sourceManaged in Haxe).value / "extraParams.hxml"
            if (!file.exists || content != IO.read(file, scala.io.Codec.UTF8.charSet)) {
              IO.write(file, content, scala.io.Codec.UTF8.charSet)
            }
            Some(file)
          }
        },
        sourceGenerators in Haxe <+= Def.task {
          Seq((makeHaxelibJson in Haxe).value)
        },
        sourceGenerators in Haxe <+= Def.task {
          (makeExtraParamsHxml in Haxe).value.toSeq
        }
      )

  override final def globalSettings = Seq(
    concurrentRestrictions += Tags.limit(SbtHaxe.HaxelibLock, 1),
    haxelibTags := Seq(),
    haxelibDependencies := Map(),
    haxeExtraParams := Seq(),
    haxeOptions := Nil,
    haxeMacros := Nil,
    haxeXmls := Nil,
    haxeNativeDependencyOptions := Nil,
    haxeCommand := "haxe",
    haxelibCommand := "haxelib"
  )

  final val HaxeUnit = new TestFramework("com.qifun.sbtHaxe.testInterface.HaxeUnitFramework")

  override final def buildSettings =
    super.buildSettings :+ (testFrameworks += HaxeUnit)

}
