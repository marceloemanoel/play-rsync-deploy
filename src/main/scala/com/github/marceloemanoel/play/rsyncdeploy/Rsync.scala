package com.github.marceloemanoel.play.rsyncdeploy

import sbt._

case class Rsync( remotePath: String,
                  serverAddress: String,
                  username: String,
                  keyFile: Option[File],
                  directory: File,
                  displayProgress: Boolean,
                  excludes: Seq[File]) {

  def execute(): ProcessBuilder = {
    val arguments = List("rsync", "-CEhravzp")
    val finalArguments = arguments ++
                         sshArguments.getOrElse(Nil) ++
                         excludedFiles ++
                         displayProgressOption ++
                         List("--delete", s"${directory.absolutePath}", s"$username@$serverAddress:${remotePath}")

    println(finalArguments)

    Process(finalArguments)
  }


  private def sshArguments(): Option[Seq[String]] = keyFile map { file =>
    List(s"-e ssh -i ${file.absolutePath}")
  }

  private def excludedFiles() = excludes map { file =>
    s"--exclude ${file.name}"
  }

  private def displayProgressOption() = if( displayProgress ) List("--progress") else Nil
}
