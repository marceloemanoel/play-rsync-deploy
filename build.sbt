import bintray.Keys._

name := """play-rsync-deploy"""

organization := """com.github.marceloemanoel"""

sbtPlugin := true

version := "1.0"

scalaVersion := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

publishMavenStyle := false

repository in bintray := "play-rsync-deploy"

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

seq(bintrayPublishSettings:_*)

bintrayOrganization in bintray := None

packageLabels in bintray := Seq("play-framework", "deploy", "rsync")

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.6" % "test"

