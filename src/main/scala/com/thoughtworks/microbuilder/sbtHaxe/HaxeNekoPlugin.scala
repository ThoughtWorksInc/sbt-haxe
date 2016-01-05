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
 * A Plugin used to compile Haxe sources to Neko binary.
 */
object HaxeNekoPlugin extends AutoPlugin {

  override final def requires = BaseHaxePlugin

  override final lazy val projectSettings: Seq[Setting[_]] = {
    sbt.addArtifact(artifact in packageBin in HaxeNeko, packageBin in HaxeNeko) ++
      inConfig(Neko)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestNeko)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeNeko)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeNeko)(SbtHaxe.extendSettings) ++
      inConfig(TestHaxeNeko)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestHaxeNeko)(SbtHaxe.extendTestSettings) ++
      SbtHaxe.injectSettings(HaxeNeko, Neko) ++
      SbtHaxe.injectSettings(TestHaxeNeko, TestNeko) ++
      (for {
        injectConfiguration <- Seq(Neko, TestNeko)
        setting <- Seq(
          haxePlatformName in injectConfiguration := "neko",
          target in haxe in injectConfiguration := (sourceManaged in injectConfiguration).value / raw"""${name.value}.n""",
          haxeOutputPath in injectConfiguration := Some((target in haxe in injectConfiguration).value)
        )
      } yield setting) ++
      Seq(
        haxeXmls in Compile ++= (haxeXml in Neko).value,
        haxeXmls in Test ++= (haxeXml in TestNeko).value,
        doxRegex in Compile := SbtHaxe.buildDoxRegex((sourceDirectories in HaxeNeko).value),
        doxRegex in Test := SbtHaxe.buildDoxRegex((sourceDirectories in TestHaxeNeko).value),
        ivyConfigurations += Haxe,
        ivyConfigurations += TestHaxe,
        ivyConfigurations += HaxeNeko,
        ivyConfigurations += TestHaxeNeko)
  }
}
