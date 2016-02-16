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
  * A Plugin used to compile Haxe sources to C++ sources.
  */
object HaxeCppPlugin extends AutoPlugin {

  override final def requires = BaseHaxePlugin

  override final lazy val projectSettings: Seq[Setting[_]] = {
    super.projectSettings ++
      sbt.addArtifact(artifact in packageBin in HaxeCpp, packageBin in HaxeCpp) ++
      inConfig(Cpp)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestCpp)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeCpp)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeCpp)(SbtHaxe.extendSettings) ++
      inConfig(TestHaxeCpp)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestHaxeCpp)(SbtHaxe.extendTestSettings) ++
      SbtHaxe.injectSettings(HaxeCpp, Cpp) ++
      SbtHaxe.injectSettings(TestHaxeCpp, TestCpp) ++
      (for {
        injectConfiguration <- Seq(Cpp, TestCpp)
        setting <- Seq(
          haxePlatformName in injectConfiguration := "cpp",
          target in haxe in injectConfiguration := (sourceManaged in injectConfiguration).value,
          haxeOutputPath in injectConfiguration := Some((target in haxe in injectConfiguration).value),
          haxeOptions in injectConfiguration ++= {
            if ((isLibrary in injectConfiguration).value) {
              Seq("-D", "static_link")
            } else {
              Seq()
            }
          }
        )
      } yield setting) ++
      Seq(
        haxeXmls in Compile ++= (haxeXml in Cpp).value,
        haxeXmls in Test ++= (haxeXml in TestCpp).value,
        doxRegex in Compile := SbtHaxe.buildDoxRegex((sourceDirectories in HaxeCpp).value),
        doxRegex in Test := SbtHaxe.buildDoxRegex((sourceDirectories in TestHaxeCpp).value),
        ivyConfigurations += Haxe,
        ivyConfigurations += TestHaxe,
        ivyConfigurations += HaxeCpp,
        ivyConfigurations += TestHaxeCpp)
  }
}
