package com.github.marceloemanoel.play.rsyncdeploy

import java.io.FileOutputStream
import scala.language.reflectiveCalls

import sbt.Keys._
import sbt._

object RsyncDeployPlugin extends AutoPlugin {

  object autoImport {

    object deploy {
      lazy val userName = settingKey[String]("username used to connect to the server")
      lazy val password = settingKey[Option[String]]("password used to connect to the server")
      lazy val passwordFile = settingKey[Option[File]]("one line file containing password")
      lazy val serverAddress = settingKey[String]("server address")
      lazy val connectionPort = settingKey[Option[Int]]("Port used to connect to the server")
      lazy val serverPort = settingKey[Option[Int]]("Port that the project will run")
      lazy val remotePath = settingKey[Option[String]]("path in the server to deploy the application. Default: /home/$userName")
      lazy val keyDir = settingKey[Option[File]]("Option defining the key file to connect to the server. Default: desployKeys")
      lazy val keyFile = settingKey[Option[File]]("Key file used for deploying")
      lazy val excludes = settingKey[Option[Seq[String]]]("List of files or folders that should be excluded from deploy")
      lazy val defaultExcludes = settingKey[Seq[String]]("Default deploy exclusion list of files and folders")
      lazy val displayProgress = settingKey[Boolean]("Displays rync progress. Default true")
    }

    lazy val rsyncDeploy = taskKey[Unit]("Connects to the server and send the files using rsync")
  }

  import autoImport._

  override def trigger(): PluginTrigger = allRequirements

  override def projectSettings(): Seq[Def.Setting[_]] = Seq(
    deploy.userName := "",
    deploy.password := None,
    deploy.passwordFile := None,
    deploy.serverAddress := "localhost",
    deploy.connectionPort := None,
    deploy.serverPort := Some(9000),
    deploy.remotePath := Some("~"),

    deploy.keyDir := {
      val keyDir = (baseDirectory.value / "deployKeys")
      if (keyDir.exists) Some(keyDir) else None
    },

    deploy.keyFile := deploy.keyDir.value.map( _ / "production.pem"),

    deploy.defaultExcludes := Seq(
      ".idea",
      ".idea_modules",
      "target",
      "project/project",
      "project/target",
      "logs",
      "test",
      "RUNNING_PID"
    ) ++ deploy.keyDir.value.map(dir => List(dir.getName)).getOrElse(Nil),

    deploy.excludes := None,
    deploy.displayProgress := true,

    rsyncDeploy := {
      implicit val log = streams.value.log
      val runScript = baseDirectory.value / "run.sh"

      if(!runScript.exists) {
        log.info("Creating run.sh script")
        copyRunScript(runScript)
      }

      if(!runScript.canExecute) {
        log.info("Making run.sh executable.")
        runScript.setExecutable(true)
      }

      val rsync = Rsync(
        remotePath = deploy.remotePath.value.get,
        serverAddress = deploy.serverAddress.value,
        username = deploy.userName.value,
        keyFile = deploy.keyFile.value,
        directory = baseDirectory.value,
        displayProgress = deploy.displayProgress.value,
        excludes = deploy.defaultExcludes.value ++ deploy.excludes.value.getOrElse(Nil)
      )

      val exitCode = rsync.execute() ! log

      if (exitCode != 0) {
        fail
      }
      else {
        val ssh = SSH(
          username = deploy.userName.value,
          password = deploy.password.value,
          identityFile = deploy.keyFile.value,
          port = deploy.connectionPort.value,
          host = deploy.serverAddress.value
        )
        val path = s"${deploy.remotePath.value.get}/${baseDirectory.value.name}"
        val command = s"""APP_DIR=${baseDirectory.value.name} PORT=${deploy.serverPort.value.get} $path/run.sh"""

        val exitCode = ssh.execute(command) ! log
        if(exitCode != 0) {
          fail
        }
      }
    }
  )

  private def copyRunScript(target: File) = {
    def use[T <: { def close(): Unit }](closable: T)(block: T => Unit) {
      try {
        block(closable)
      }
      finally {
        closable.close()
      }
    }

    use(getClass.getResourceAsStream("/run.sh")) { input =>
      use(new FileOutputStream(target)) { output =>
        val buffer = new Array[Byte](1024)
        Iterator.continually(input.read(buffer))
                .takeWhile(_ != -1)
                .foreach { output.write(buffer, 0 , _) }
      }
    }
  }
}