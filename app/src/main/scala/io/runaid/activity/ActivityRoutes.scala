package io.runaid.activity

import cats.effect.Sync
import fs2.Pipe
import fs2.Stream
import io.circe.Codec
import io.runaid.eventstream.ActivityLog
import io.runaid.eventstream.EventStreamEvent
import io.runaid.http.HttpRouter
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import cats.implicits._
import io.scalaland.chimney.dsl._

object ActivityRoutes {

  def make[F[_]: Sync](publishEvents: Pipe[F, EventStreamEvent, Unit]): HttpRouter[F] = HttpRouter.make { dsl =>
    import dsl._

    HttpRoutes.of {
      case req @ POST -> Root / "log" =>
        def recordLog(request: LogRequest): F[Unit] =
          Stream.emit(EventStreamEvent.ActivityLogged(request.transformInto[ActivityLog])).through(publishEvents).compile.drain

        for {
          logRequest <- req.as[LogRequest]
          _          <- recordLog(logRequest)
          response   <- Created()
        } yield response

    }
  }
}

final case class LogRequest(lat: Long, long: Long)

object LogRequest {
  implicit val codec: Codec[LogRequest] = io.circe.generic.semiauto.deriveCodec
}
