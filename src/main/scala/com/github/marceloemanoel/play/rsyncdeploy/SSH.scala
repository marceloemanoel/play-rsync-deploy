package com.github.marceloemanoel.play.rsyncdeploy

import sbt.{File, Process}

case class SSH(username: String,
               password: Option[String],
               identityFile: Option[File],
               host: String,
               port: Option[Int]) {


  def execute(command: String = "") = {
    val arguments = List("ssh") ++
                    identity ++
                    portNumber ++
                    userAtHost ++
                    List(command)

    println(arguments)

    Process(arguments)
  }

  private def identity() =
    identityFile.map(file => List("-i", s"${file.getAbsolutePath}"))
                .getOrElse(Nil)


  private def portNumber() =
    port.map(number => List(s"-p$number"))
        .getOrElse(Nil)

  private def userAtHost() =
    List(s"${username}@${host}")
}
