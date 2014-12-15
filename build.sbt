import bintray.Keys._
import com.typesafe.sbt.SbtGit._

versionWithGit

git.baseVersion := "0.1"

name := """play-rsync-deploy"""

organization := """com.github.marceloemanoel"""

sbtPlugin := true

scalaVersion := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

publishMavenStyle := false

repository in bintray := "sbt-plugin-releases"

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

Seq(bintraySettings:_*)

Seq(bintrayPublishSettings:_*)

bintrayOrganization in bintray := None

packageLabels in bintray := Seq("play-framework", "deploy", "rsync")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.6" % "test"
)

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.8.0-RC2")
