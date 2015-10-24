sbtPlugin := true

name := "sbt-haxe"

organization := "com.thoughtworks.microbuilder"

releaseUseGlobalVersion := false

scalacOptions += "-deprecation"

scalacOptions += "-feature"

description := "A Sbt plugin used to compile Haxe sources in Java/Scala projects."

homepage := Some(url("https://github.com/ThoughtWorksInc/sbt-haxe"))

startYear := Some(2014)

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

releasePublishArtifactsAction := PgpKeys.publishSigned.value

import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeRelease"),
  pushChanges
)

scmInfo := Some(ScmInfo(
  url("https://bitbucket.org/qforce/sbt-haxe"),
  "scm:git:https://bitbucket.org/qforce/ai-demo.git",
  Some("scm:git:git@bitbucket.org:qforce/sbt-haxe.git")))

developers := List(
  Developer(
    "chank",
    "方里权",
    "fangliquan@qq.com",
    url("https://github.com/chank")
  ),
  Developer(
    "Atry",
    "杨博 (Yang Bo)",
    "pop.atry@gmail.com",
    url("https://github.com/Atry")
  )
)
