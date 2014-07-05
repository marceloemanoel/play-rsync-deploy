name := """play-rsync-deploy"""

organization := """com.github.marceloemanoel"""

sbtPlugin := true

version := "1.0"

scalaVersion := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.6" % "test"

