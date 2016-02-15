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

/**
  * A plugin used to compile Haxe sources to CSharp sources.
  */
final object HaxeCSharpPlugin extends AutoPlugin {

  override final def requires = BaseHaxePlugin

  override final lazy val projectSettings: Seq[Setting[_]] =
    super.projectSettings ++
      sbt.addArtifact(artifact in packageBin in HaxeCSharp, packageBin in HaxeCSharp) ++
      inConfig(CSharp)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestCSharp)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeCSharp)(SbtHaxe.baseHaxeSettings) ++
      inConfig(HaxeCSharp)(SbtHaxe.extendSettings) ++
      inConfig(TestHaxeCSharp)(SbtHaxe.baseHaxeSettings) ++
      inConfig(TestHaxeCSharp)(SbtHaxe.extendTestSettings) ++
      SbtHaxe.injectSettings(HaxeCSharp, CSharp) ++
      SbtHaxe.injectSettings(TestHaxeCSharp, TestCSharp) ++
      SbtHaxe.csharpRunSettings(CSharp) ++
      SbtHaxe.csharpRunSettings(TestCSharp) ++
      (for {
        injectConfiguration <- Seq(CSharp, TestCSharp)
        setting <- Seq(
          haxePlatformName in injectConfiguration := "cs",
          target in haxe in injectConfiguration := (sourceManaged in injectConfiguration).value,
          haxeOutputPath in injectConfiguration := Some((target in haxe in injectConfiguration).value),
          haxeOptions in injectConfiguration ++= {
            if (isLibrary.value) {
              Seq("-D", "dll")
            } else {
              Seq()
            }
          }
        )
      } yield setting) ++
      Seq(
        haxeXmls in Compile ++= (haxeXml in CSharp).value,
        haxeXmls in Test ++= (haxeXml in TestCSharp).value,
        doxRegex in Compile := SbtHaxe.buildDoxRegex((sourceDirectories in HaxeCSharp).value),
        doxRegex in Test := SbtHaxe.buildDoxRegex((sourceDirectories in TestHaxeCSharp).value),
        ivyConfigurations += Haxe,
        ivyConfigurations += TestHaxe,
        ivyConfigurations += HaxeCSharp,
        ivyConfigurations += TestHaxeCSharp)

}