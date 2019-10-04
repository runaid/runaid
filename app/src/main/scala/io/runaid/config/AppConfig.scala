package io.runaid.config

import pureconfig.ConfigReader

final case class AppConfig(http: Http)

object AppConfig {
  implicit val reader: ConfigReader[AppConfig] = {
    import pureconfig.generic.auto._

    exportReader[AppConfig].instance
  }
}

final case class Http(port: Int)
