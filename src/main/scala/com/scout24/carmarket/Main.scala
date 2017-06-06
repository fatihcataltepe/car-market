package com.scout24.carmarket

import com.typesafe.config.ConfigFactory

object Main {
  def main(args: Array[String]): Unit = {
    val props: WebServerProps = WebServerProps(ConfigFactory.load())
    val webServer: WebServer = WebServer(props)
    webServer.run
  }

}
