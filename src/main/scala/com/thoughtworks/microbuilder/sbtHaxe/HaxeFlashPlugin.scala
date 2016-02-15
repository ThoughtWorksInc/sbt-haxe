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
  * A Plugin used to compile Haxe sources to Flash swf file.
  */
object HaxeFlashPlugin extends AutoPlugin {

  override final def requires = BaseHaxePlugin

  override final lazy val projectSettings: Seq[Setting[_]] = {
    super.projectSettings ++
      sbt.addArtifact(artifact in packageBin in HaxeFlash, packageBin in HaxeFlash) ++
      inConfig(Flash)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestFlash)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeFlash)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeFlash)(SbtHaxe.extendSettings) ++
      inConfig(TestHaxeFlash)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestHaxeFlash)(SbtHaxe.extendTestSettings) ++
      SbtHaxe.injectSettings(HaxeFlash, Flash) ++
      SbtHaxe.injectSettings(TestHaxeFlash, TestFlash) ++
      (for {
        injectConfiguration <- Seq(Flash, TestFlash)
        setting <- Seq(
          haxePlatformName in injectConfiguration := "swf",
          target in haxe in injectConfiguration := {
            if (isLibrary.value) {
              (sourceManaged in injectConfiguration).value / raw"""${name.value}.swc"""
            } else {
              (sourceManaged in injectConfiguration).value / raw"""${name.value}.swf"""
            }
          },
          haxeOutputPath in injectConfiguration := Some((target in haxe in injectConfiguration).value)
        )
      } yield setting) ++
      Seq(
        haxeXmls in Compile ++= (haxeXml in Flash).value,
        haxeXmls in Test ++= (haxeXml in TestFlash).value,
        doxRegex in Compile := SbtHaxe.buildDoxRegex((sourceDirectories in HaxeFlash).value),
        doxRegex in Test := SbtHaxe.buildDoxRegex((sourceDirectories in TestHaxeFlash).value),
        ivyConfigurations += Haxe,
        ivyConfigurations += TestHaxe,
        ivyConfigurations += HaxeFlash,
        ivyConfigurations += TestHaxeFlash)
  }

}
