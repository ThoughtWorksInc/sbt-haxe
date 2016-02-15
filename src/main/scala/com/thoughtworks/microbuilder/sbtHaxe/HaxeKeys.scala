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


  type DependencyVersion = com.thoughtworks.microbuilder.sbtHaxe.DependencyVersion
  final val DependencyVersion = com.thoughtworks.microbuilder.sbtHaxe.DependencyVersion

  final val haxelibContributors = SettingKey[Seq[String]]("haxelib-contributors", "Contributors in haxelib.json")

  final val haxelibSubmitUsername = SettingKey[String]("haxelib-submit-username", "The username for `haxelib submit`")

  final val haxelibSubmitPassword = SettingKey[String]("haxelib-submit-password", "The password for `haxelib submit`")

  final val haxelibReleaseNote = SettingKey[String]("haxelib-release-note", "The release note in haxelib.json")

  final val haxelibTags = SettingKey[Seq[String]]("haxelib-tags", "Tags in haxelib.json")

  final val haxelibDependencies = SettingKey[Map[String, DependencyVersion]]("haxelib-dependencies", "Additional dependencies in haxelib.json")

  final val haxelibInstallDependencies = TaskKey[Unit]("haxelib-install-dependencies", "Install additional dependencies in haxelib.json")

  final val haxelibJson = SettingKey[JSONObject]("haxelib-json", "The file content of haxelib.json")

  final val makeHaxelibJson = TaskKey[File]("make-haxelib-json", "Create haxelib.json")

  final val haxeExtraParams = SettingKey[Seq[String]]("haxe-extra-params", "The extra haxe flags in extraParams.hxml")

  final val makeExtraParamsHxml = TaskKey[Option[File]]("make-extra-params-hxml", "Create extraParams.hxml")

  final val haxeNativeDependencyOptions = TaskKey[Seq[String]]("haxe-native-dependency-options", "-java-lib or -net-lib options for Haxe compiler.")

  final val isLibrary = SettingKey[Boolean]("is-library", "Indicate whether the current Haxe project is a library or a executable. The Haxe compiler generate DLL for C# target, static library for C++ target and SWC for Flash target, if `isLibrary` is true.")
}

final object HaxeKeys extends HaxeKeys
