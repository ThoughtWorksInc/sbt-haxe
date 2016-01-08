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

import HaxeConfigurations._
import HaxeKeys._
import sbt.Keys._
import sbt._

/**
  * A Plugin used to compile Haxe sources to Action Script 3 sources.
  */
object HaxeAs3Plugin extends AutoPlugin {

  override final def requires = BaseHaxePlugin

  override final lazy val projectSettings: Seq[Setting[_]] = {
    super.projectSettings ++
      sbt.addArtifact(artifact in packageBin in HaxeAs3, packageBin in HaxeAs3) ++
      inConfig(As3)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestAs3)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeAs3)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeAs3)(SbtHaxe.extendSettings) ++
      inConfig(TestHaxeAs3)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestHaxeAs3)(SbtHaxe.extendTestSettings) ++
      SbtHaxe.injectSettings(HaxeAs3, As3) ++
      SbtHaxe.injectSettings(TestHaxeAs3, TestAs3) ++
      (for {
        injectConfiguration <- Seq(As3, TestAs3)
        setting <- Seq(
          haxePlatformName in injectConfiguration := "as3",
          target in haxe in injectConfiguration := (sourceManaged in injectConfiguration).value,
          haxeOutputPath in injectConfiguration := Some((target in haxe in injectConfiguration).value)
        )
      } yield setting) ++
      Seq(
        haxeXmls in Compile ++= (haxeXml in As3).value,
        haxeXmls in Test ++= (haxeXml in TestAs3).value,
        doxRegex in Compile := SbtHaxe.buildDoxRegex((sourceDirectories in HaxeAs3).value),
        doxRegex in Test := SbtHaxe.buildDoxRegex((sourceDirectories in TestHaxeAs3).value),
        ivyConfigurations += Haxe,
        ivyConfigurations += TestHaxe,
        ivyConfigurations += HaxeAs3,
        ivyConfigurations += TestHaxeAs3)
  }
}
