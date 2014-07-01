package com.github.marceloemanoel.play.rsyncdeploy

import sbt.Keys._
import sbt._

object RsyncDeployPlugin extends AutoPlugin {

  object autoImport {
    
    object deploy {
      lazy val userName = settingKey[String]("username used to connect to the server")
      lazy val password = settingKey[Option[String]]("password used to connect to the server")
      lazy val serverAddress = settingKey[String]("server address")
      lazy val serverPort = settingKey[Int]("Port that the project will run")
      lazy val remotePath = settingKey[String]("path in the server to deploy the application")
      lazy val keyDir = settingKey[Option[File]]("Option defining the key file to connect to the server. Default: desployKeys")
      lazy val keyFile = settingKey[String]("Key file used for deploying")
    }
    
    val rsyncDeploy = taskKey[Unit]("Connects to the server and send the files using rsync")
    
    rsyncDeploy := {
      val log = sLog.value
      val username = deploy.userName.value
      val serverAddress = deploy.serverAddress.value
      val remotePath = deploy.remotePath.value
      val keyDir: File = deploy.keyDir.value.getOrElse(baseDirectory.value / "deployKeys")
      val keyFile = keyDir / deploy.keyFile.value
      val serverPort = deploy.serverPort.value

      Process(List("rsync", "-CEhravzp",
                            "-e", s"ssh -i ${keyFile.absolutePath}", //connect using ssh
                            "--exclude", ".idea", //do not send .idea folder
                            "--exclude", ".idea_modules", //do not send .idea_modules folder
                            "--exclude", "target", //do not send the target folder
                            "--exclude", "logs", //do not send the log folder
                            "--exclude", s"${keyDir.name}", //do not send key dir
                            "--exclude", "test", //do not send test folder
                            "--progress", //Show us some progress during transfer
                            "--delete", //Removes extra files on destination
                            s"${baseDirectory.value.absolutePath}",
                            s"$username@$serverAddress:${remotePath}")) ! log

      Process(List("ssh", "-i", keyFile.absolutePath,
                        s"$username@$serverAddress",
                        s"~/${baseDirectory.value.name}/run.sh ${baseDirectory.value.name} $serverPort")) ! log
    }
  }

}