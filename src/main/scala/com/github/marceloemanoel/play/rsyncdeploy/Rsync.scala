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
    val arguments = List("rsync", "-Chravzp", "--executability") ++
                    sshArguments ++
                    excludedFiles ++
                    displayProgressOption ++
                    List("--delete", s"${directory.absolutePath}", s"$username@$serverAddress:${remotePath}")

    println(arguments.mkString(" "))

    Process(arguments)
  }


  private def sshArguments(): Seq[String] =
    keyFile map { file =>
      List(s"-e ssh -i ${file.absolutePath}")
    } getOrElse(Nil)

  private def excludedFiles() = {
    val exclusionString = excludes map { file => s"--exclude '${file.name}'" } mkString(" ")
    exclusionString.split(" ")
  }

  private def displayProgressOption() =
    if (displayProgress) List("--progress") else Nil
}
