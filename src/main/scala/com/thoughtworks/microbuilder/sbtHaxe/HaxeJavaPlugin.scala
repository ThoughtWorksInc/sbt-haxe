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
import Keys._
import HaxeKeys._
import HaxeConfigurations._
import sbt.AutoPlugin

/**
 * A Plugin used to compile Haxe sources to Java sources.
 */
final object HaxeJavaPlugin extends AutoPlugin {

  override final def requires = BaseHaxePlugin

  override final def trigger = allRequirements

  override final lazy val projectSettings: Seq[Setting[_]] =
    sbt.addArtifact(artifact in packageBin in HaxeJava, packageBin in HaxeJava) ++
      inConfig(HaxeJava)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeJava)(SbtHaxe.extendSettings) ++
      inConfig(TestHaxeJava)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestHaxeJava)(SbtHaxe.extendTestSettings) ++
      SbtHaxe.injectSettings(HaxeJava, Compile) ++
      SbtHaxe.injectSettings(TestHaxeJava, Test) ++
      Seq(
        haxeXmls in Compile ++= (haxeXml in Compile).value,
        haxeXmls in Test ++= (haxeXml in Test).value,
        haxePlatformName in Compile := "java",
        haxeOutputPath in Compile := None,
        haxeOptions in Compile ++= Seq("-D", "no-compilation"),
        doxRegex in Compile := SbtHaxe.buildDoxRegex((sourceDirectories in HaxeJava).value),
        doxRegex in Test := SbtHaxe.buildDoxRegex((sourceDirectories in TestHaxeJava).value),
        ivyConfigurations += Haxe,
        ivyConfigurations += TestHaxe,
        ivyConfigurations += HaxeJava,
        ivyConfigurations += TestHaxeJava)

}