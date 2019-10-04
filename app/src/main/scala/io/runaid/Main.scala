package io.runaid

import cats.effect.ConcurrentEffect
import cats.effect.ContextShift
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import cats.effect.Timer
import fs2.concurrent.Topic
import io.runaid.activity.ActivityRoutes
import io.runaid.config.AppConfig
import io.runaid.eventstream.EventStreamEvent
import io.runaid.eventstream.EventStreamRoutes
import io.runaid.extensions.all._
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder
import pureconfig.ConfigSource

import scala.concurrent.ExecutionContext

class Application[F[_]: ConcurrentEffect: Timer: ContextShift](config: AppConfig)(implicit executionContext: ExecutionContext) {

  def makeServer(router: HttpRoutes[F]): Resource[F, Server[F]] =
    BlazeServerBuilder[F]
      .withWebSockets(true)
      .withHttpApp(router.orNotFound)
      .withExecutionContext(executionContext)
      .bindHttp(config.http.port, "0.0.0.0")
      .resource

  val server: Resource[F, Server[F]] = Topic[F, EventStreamEvent](EventStreamEvent.Dummy).liftResource.flatMap { eventTopic =>
    makeServer(
      Router(
        "/events" -> EventStreamRoutes.make[F](eventTopic.subscribe(100).tail).routes,
        "/activity" -> ActivityRoutes.make[F](eventTopic.publish).routes
      )
    )
  }
}

object Main extends IOApp {
  implicit val ec: ExecutionContext = ExecutionContext.global

  private val loadConfig: IO[AppConfig] = IO(ConfigSource.default.loadOrThrow[AppConfig])

  override def run(args: List[String]): IO[ExitCode] = {
    val app = for {
      config <- loadConfig.liftResource
      server <- new Application[IO](config).server
    } yield server

    app.use(_ => IO.never)
  }
}
