package com.scout24.carmarket

import com.typesafe.config.Config

case class WebServerProps(host: String, port: Int)

object WebServerProps {
  def apply(config: Config): WebServerProps = {
    val conf: Config = config.getConfig("car-market")
    WebServerProps(
      host = conf.getString("ws-host"),
      port = conf.getInt("ws-port")
    )
  }
}
