package com.scout24.carmarket

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

object Main extends LazyLogging{
  def main(args: Array[String]): Unit = {
    logger.info("Scout24-car-market app is started")
    val props: WebServerProps = WebServerProps(ConfigFactory.load())
    val webServer: WebServer = WebServer(props)

    logger.info("Starting webserver")
    webServer.run
  }

}
