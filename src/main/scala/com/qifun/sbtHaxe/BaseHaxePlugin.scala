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

package com.qifun.sbtHaxe

import java.io.{PipedOutputStream, PipedInputStream}

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

  final object autoImport extends HaxeKeys with HaxeConfigurations

  import autoImport._

  private val WhichUserRegex = """Which of these users are you: \[(.*)\]""".r

  override final lazy val projectSettings: Seq[Setting[_]] =
    inConfig(Haxe)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestHaxe)(SbtHaxe.baseHaxeSettings) ++
      SbtHaxe.docSetting(Haxe, Compile) ++
      SbtHaxe.docSetting(TestHaxe, Test) ++
      Seq(
        publish in Haxe := {
          val logger = (streams in publishLocal in Haxe).value.log
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
              throw new MessageOnlyException("Failed to submit to haxelib: " + result)
          }
        },
        publishLocal in Haxe := {
          val logger = (streams in publishLocal in Haxe).value.log
          val processHaxelibLocal = Seq(
            haxelibCommand.value,
            "local",
            (packageBin in Haxe).value.toString
          )
          logger.info(processHaxelibLocal.mkString("\"", "\" \"", "\""))
          processHaxelibLocal !< logger match {
            case 0 =>
            case result =>
              throw new MessageOnlyException("Failed to install local haxelib: " + result)
          }
        },
        haxelibContributors := developers.value.map(_.id),
        haxelibJson := JSONObject(Map(
          Seq(
            name.?.value.map("name" -> _),
            homepage.value.map("url" -> _.toString),
            licenses.?.value.flatMap(_.unzip._1.headOption.map("license" -> _)),
            haxelibTags.?.value.map("tags" -> _),
            description.?.value.map("description" -> _),
            version.?.value.map("version" -> _),
            haxelibReleaseNote.?.value.map("releasenote" -> _),
            Some("contributors" -> JSONArray(haxelibContributors.value.toList)),
            Some("dependencies" -> JSONObject(haxelibDependencies.value.mapValues {
              case LastVersion => ""
              case SpecificVersion(v) => v
            }))
          ).flatten: _*
        )),
        makeHaxelibJson := {
          val file = (sourceManaged in Haxe).value / "haxelib.json"
          IO.write(file, haxelibJson.value.toString(), scala.io.Codec.UTF8.charSet)
          file
        },
        makeExtraParamsHxml := {
          val file = (sourceManaged in Haxe).value / "extraParams.hxml"
          IO.write(file, haxeExtraParams.value.mkString("\"", "\"\n\"", "\""), scala.io.Codec.UTF8.charSet)
          file
        },
        sourceGenerators in Haxe <+= Def.task {
          Seq((makeHaxelibJson in Haxe).value)
        },
        sourceGenerators in Haxe <+= Def.task {
          Seq((makeExtraParamsHxml in Haxe).value)
        }
      )

  override final def globalSettings =
    super.globalSettings ++ Seq(
      haxelibDependencies := Map(),
      haxeExtraParams := Seq(),
      haxeOptions := Nil,
      haxeMacros := Nil,
      haxeXmls := Nil,
      haxeCommand := "haxe",
      haxelibCommand := "haxelib")

  final val HaxeUnit = new TestFramework("com.qifun.sbtHaxe.testInterface.HaxeUnitFramework")

  override final def buildSettings =
    super.buildSettings :+ (testFrameworks += HaxeUnit)

}