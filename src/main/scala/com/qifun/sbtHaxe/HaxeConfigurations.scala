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
import Keys._

trait HaxeConfigurations {

  final lazy val Haxe = config("haxe")
  final lazy val TestHaxe = config("test-haxe") extend Haxe

  final lazy val HaxeJava = config("haxe-java") extend Haxe
  final lazy val TestHaxeJava = config("test-haxe-java") extend HaxeJava

  final lazy val CSharp = config("csharp")
  final lazy val TestCSharp = config("test-csharp") extend CSharp

  final lazy val HaxeCSharp = config("haxe-csharp") extend Haxe
  final lazy val TestHaxeCSharp = config("test-haxe-csharp") extend HaxeCSharp

  final lazy val Cpp = config("cpp")
  final lazy val TestCpp = config("test-cpp") extend Cpp

  final lazy val HaxeCpp = config("haxe-cpp") extend Haxe
  final lazy val TestHaxeCpp = config("test-haxe-cpp") extend HaxeCpp

  final lazy val Js = config("js")
  final lazy val TestJs = config("test-js") extend Js

  final lazy val HaxeJs = config("haxe-js") extend Haxe
  final lazy val TestHaxeJs = config("test-haxe-js") extend HaxeJs

  final lazy val Php = config("php")
  final lazy val TestPhp = config("test-php") extend Php

  final lazy val HaxePhp = config("haxe-php") extend Haxe
  final lazy val TestHaxePhp = config("test-haxe-php") extend HaxePhp

  final lazy val Python = config("python")
  final lazy val TestPython = config("test-python") extend Python

  final lazy val HaxePython = config("haxe-python") extend Haxe
  final lazy val TestHaxePython = config("test-haxe-python") extend HaxePython

  final lazy val Neko = config("neko")
  final lazy val TestNeko = config("test-neko") extend Neko

  final lazy val HaxeNeko = config("haxe-neko") extend Haxe
  final lazy val TestHaxeNeko = config("test-haxe-neko") extend HaxeNeko

  final lazy val Flash = config("flash")
  final lazy val TestFlash = config("test-flash") extend Flash

  final lazy val HaxeFlash = config("haxe-flash") extend Haxe
  final lazy val TestHaxeFlash = config("test-haxe-flash") extend HaxeFlash

  final lazy val As3 = config("as3")
  final lazy val TestAs3 = config("test-as3") extend As3

  final lazy val HaxeAs3 = config("haxe-as3") extend Haxe
  final lazy val TestHaxeAs3 = config("test-haxe-as3") extend HaxeAs3

  final lazy val AllTargetConfigurations = Seq(
    Compile,
    CSharp,
    Cpp,
    Js,
    Php,
    Python,
    Neko,
    Flash,
    As3)

  final lazy val AllTestTargetConfigurations = Seq(
    Test,
    TestCSharp,
    TestCpp,
    TestJs,
    TestPhp,
    TestPython,
    TestNeko,
    TestFlash,
    TestAs3)

  final lazy val AllHaxeConfigurations = Seq(
    HaxeJava,
    HaxeCSharp,
    HaxeCpp,
    HaxeJs,
    HaxePhp,
    HaxePython,
    HaxeNeko,
    HaxeFlash,
    HaxeAs3)

  final lazy val AllTestHaxeConfigurations = Seq(
    TestHaxeJava,
    TestHaxeCSharp,
    TestHaxeCpp,
    TestHaxeJs,
    TestHaxePhp,
    TestHaxePython,
    TestHaxeNeko,
    TestHaxeFlash,
    TestHaxeAs3)

}

final object HaxeConfigurations extends HaxeConfigurations