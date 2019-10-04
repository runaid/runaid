package io.runaid

import cats.effect.Blocker
import cats.effect.ConcurrentEffect
import cats.effect.ContextShift
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import cats.effect.Timer
import io.runaid.config.AppConfig
import org.http4s.HttpApp
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext
import extensions.all._
import pureconfig.ConfigSource
import cats.implicits._

class Application[F[_]: ConcurrentEffect: Timer: ContextShift](
  config: AppConfig,
  blocker: Blocker
)(
  implicit executionContext: ExecutionContext
) {

  def server(): Resource[F, Server[F]] =
    BlazeServerBuilder[F]
      .withHttpApp(HttpApp.notFound[F])
      .withExecutionContext(executionContext)
      .bindHttp(config.http.port, "0.0.0.0")
      .resource
}

object Main extends IOApp {
  implicit val ec: ExecutionContext = ExecutionContext.global

  private val loadConfig: IO[AppConfig] = IO(ConfigSource.default.loadOrThrow[AppConfig])

  override def run(args: List[String]): IO[ExitCode] = {
    val app = for {
      blocker <- Blocker[IO]
      config  <- loadConfig.liftResource
      server  <- new Application[IO](config, blocker).server()
    } yield server

    app.use(_ => IO.never)
  }
}
