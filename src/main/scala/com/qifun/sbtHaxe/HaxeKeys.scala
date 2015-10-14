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

import sbt._
import java.io.File

import scala.util.parsing.json.JSONObject

trait HaxeKeys {

  final val haxeMacros = SettingKey[Seq[String]]("haxe-macros", "--macro command-line options for Haxe compiler.")
  final val haxeOptions = SettingKey[Seq[String]]("haxe-options", "Additional command-line options for Haxe compiler.")
  final val haxeCommand = SettingKey[String]("haxe-command", "The Haxe executable.")
  final val haxelibCommand = SettingKey[String]("haxelib-command", "The haxelib executable")
  final val haxePlatformName = SettingKey[String]("haxe-platform-name", "The name of the haxe platform")
  final val haxe = TaskKey[Seq[File]]("haxe", "Convert Haxe source code to target source code.")
  final val haxeOutputPath = SettingKey[Option[File]]("haxe-output-path", "The path where the Haxe code will be compiled to.")

  final val haxeXmls = TaskKey[Seq[File]]("haxe-xmls", "Generate Haxe xmls.")
  final val doxRegex = TaskKey[Seq[String]]("dox-regex", "The Regex that used to generate Haxe documentation.")
  final val haxeXml = TaskKey[Seq[File]]("haxeXml", "Generate Haxe xml.")


  type DependencyVersion = com.qifun.sbtHaxe.DependencyVersion
  val DependencyVersion = com.qifun.sbtHaxe.DependencyVersion

  val haxelibContributors = SettingKey[Seq[String]]("haxelib-contributors", "Contributors in haxelib.json")

  val haxelibSubmitContributorIndex = SettingKey[Int]("haxelib-submit-contributor-index", "The index in haxelib-contributors that indicates the username for `haxelib submit`")

  val haxelibSubmitPassword = SettingKey[String]("haxelib-submit-password", "The password for `haxelib submit`")
  
  val haxelibReleaseNote = SettingKey[String]("haxelib-release-note", "The release note in haxelib.json")

  val haxelibTags = SettingKey[String]("haxelib-tags", "Tags in haxelib.json")

  val haxelibDependencies = SettingKey[Map[String, DependencyVersion]]("haxelib-dependencies", "Additional dependencies in haxelib.json")

  val haxelibJson = SettingKey[JSONObject]("haxelib-json", "The file content of haxelib.json")

  val makeHaxelibJson = TaskKey[File]("make-haxelib-json", "Create haxelib.json")

  val haxeExtraParams = SettingKey[Seq[String]]("haxe-extra-params", "The extra haxe flags in extraParams.hxml")

  val makeExtraParamsHxml = TaskKey[File]("make-extra-params-hxml", "Create extraParams.hxml")
}

final object HaxeKeys extends HaxeKeys