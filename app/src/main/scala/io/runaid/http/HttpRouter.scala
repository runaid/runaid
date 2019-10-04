package io.runaid.http

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

trait HttpRouter[F[_]] {
  def routes: HttpRoutes[F]
}

object HttpRouter {

  def make[F[_]](mkRouter: Http4sDsl[F] => HttpRoutes[F]): HttpRouter[F] = new HttpRouter[F] {
    override val routes: HttpRoutes[F] = mkRouter(new Http4sDsl[F] {})
  }
}
