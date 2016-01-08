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
  * A Plugin used to compile Haxe sources to JavaScript sources.
  */
object HaxeJsPlugin extends AutoPlugin {

  override final def requires = BaseHaxePlugin

  override final lazy val projectSettings: Seq[Setting[_]] = {
    super.projectSettings ++
      sbt.addArtifact(artifact in packageBin in HaxeJs, packageBin in HaxeJs) ++
      inConfig(Js)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestJs)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeJs)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeJs)(SbtHaxe.extendSettings) ++
      inConfig(TestHaxeJs)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestHaxeJs)(SbtHaxe.extendTestSettings) ++
      SbtHaxe.injectSettings(HaxeJs, Js) ++
      SbtHaxe.injectSettings(TestHaxeJs, TestJs) ++
      (for {
        injectConfiguration <- Seq(Js, TestJs)
        setting <- Seq(
          haxePlatformName in injectConfiguration := "js",
          target in haxe in injectConfiguration := (sourceManaged in injectConfiguration).value / raw"""${name.value}.js""",
          haxeOutputPath in injectConfiguration := Some((target in haxe in injectConfiguration).value)
        )
      } yield setting) ++
      Seq(
        haxeXmls in Compile ++= (haxeXml in Js).value,
        haxeXmls in Test ++= (haxeXml in TestJs).value,
        doxRegex in Compile := SbtHaxe.buildDoxRegex((sourceDirectories in HaxeJs).value),
        doxRegex in Test := SbtHaxe.buildDoxRegex((sourceDirectories in TestHaxeJs).value),
        ivyConfigurations += Haxe,
        ivyConfigurations += TestHaxe,
        ivyConfigurations += HaxeJs,
        ivyConfigurations += TestHaxeJs)
  }

}
