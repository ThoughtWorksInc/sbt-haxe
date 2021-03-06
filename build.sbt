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

releaseProcess := {
  releaseProcess.value.patch(releaseProcess.value.indexOf(pushChanges), Seq[ReleaseStep](releaseStepCommand("sonatypeRelease")), 0)
}

releaseProcess -= runClean

releaseProcess -= runTest

scmInfo := Some(ScmInfo(
  url(s"https://github.com/ThoughtWorksInc/${name.value}"),
  s"scm:git:git://github.com/ThoughtWorksInc/${name.value}.git",
  Some(s"scm:git:git@github.com:ThoughtWorksInc/${name.value}.git")))

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
  ),
  Developer(
    "zhanglongyang",
    "张龙洋",
    "longyang.zhang@rea-group.com",
    url("https://github.com/zhanglongyang")
  )
)

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.2"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

pgpSecretRing := baseDirectory.value / "secret" / "secring.asc"

pgpPublicRing := baseDirectory.value / "pubring.asc"

pgpPassphrase := Some(Array.empty)
