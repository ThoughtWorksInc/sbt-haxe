package com.thoughtworks.microbuilder.sbtHaxe

import java.io.{FileInputStream, BufferedInputStream, FileOutputStream, BufferedOutputStream}
import java.util.zip.GZIPOutputStream

import com.thoughtworks.microbuilder.sbtHaxe.HaxeConfigurations._
import com.thoughtworks.microbuilder.sbtHaxe.HaxeKeys._
import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveOutputStream}
import org.apache.commons.io.IOUtils
import sbt.Keys._
import sbt._

import scala.util.parsing.json.{JSONArray, JSONObject}

/**
  * Create NPM package from Haxe generated JavaScript
  *
  * @author 杨博 (Yang Bo) &lt;pop.atry@gmail.com&gt;
  */
object HaxeJsNpmPlugin extends AutoPlugin {

  override final def requires = HaxeJsPlugin

  object autoImport {

    final val npmDependencies = SettingKey[Map[String, String]]("npm-dependencies", "Key/value paris in dependencies section in package.json")

    final val packageNpm = TaskKey[File]("package-npm", "Create NPM package for Haxe compiled JavaScript.")

  }

  import autoImport._

  override final lazy val globalSettings = {
    super.globalSettings ++
      Seq(
        npmDependencies := Map.empty
      )
  }

  override final lazy val projectSettings: Seq[Setting[_]] = {
    super.projectSettings ++
      sbt.addArtifact(artifact in packageNpm in HaxeJs, packageNpm in HaxeJs) ++
      inConfig(HaxeJs)(Defaults.packageTaskSettings(
        packageNpm,
        Def.task[Seq[(File, String)]] {
          val packageDirectory = (crossTarget in packageNpm in HaxeJs).value
          packageDirectory.mkdirs()
          val packageJsonFile = packageDirectory / "package.json"
          val packageJson = JSONObject(Map(
            Seq(
              name.?.value.map("name" -> _),
              homepage.value.map("url" -> _.toString),
              licenses.?.value.flatMap(_.unzip._1.headOption.map("license" -> _)),
              Some("keywords" -> JSONArray(haxelibTags.value.toList)),
              description.?.value.map("description" -> _),
              version.?.value.map("version" -> _),
              Some("contributors" -> JSONArray(haxelibContributors.value.toList)),
              Some("dependencies" -> JSONObject(npmDependencies.value))
            ).flatten: _*
          ))
          IO.write(packageJsonFile, packageJson.toString())
          val Seq(jsFile) = (haxe in Js).value
          Seq(
            packageJsonFile -> "./package.json",
            jsFile -> s"./index.js"
          )
        })) ++
      Seq(
        artifactClassifier in packageNpm in HaxeJs := Some("npm"),
        artifact in packageNpm in HaxeJs := {
          val originalArtifact = (artifact in packageNpm in HaxeJs).value
          import originalArtifact._
          Artifact(name, `type`, "tgz", classifier, configurations, url, extraAttributes)
        },
        packageNpm in HaxeJs := {
          val configuration = (packageConfiguration in packageNpm in HaxeJs).value
          val logger = (streams in packageNpm in HaxeJs).value.log
          logger.info(s"Make NPM package ${configuration.jar}...")
          val gzipStream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(configuration.jar)))
          try {
            val taos = new TarArchiveOutputStream(gzipStream)
            taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU)
            for ((file, name) <- configuration.sources) {
              taos.putArchiveEntry(new TarArchiveEntry(file, name))
              val bis = new BufferedInputStream(new FileInputStream(file))
              try {
                IOUtils.copy(new FileInputStream(file), taos)
                taos.closeArchiveEntry()
              } finally {
                bis.close()
              }
            }
            taos.close()
          } finally {
            gzipStream.close()
          }
          configuration.jar
        }
      )
  }
}
