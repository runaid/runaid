package io.runaid.eventstream

import cats.effect.Sync
import io.runaid.http.HttpRouter
import org.http4s.HttpRoutes
import org.http4s.server.websocket.WebSocketBuilder
import fs2.Pipe
import fs2.Stream
import io.circe.Encoder
import org.http4s.websocket.WebSocketFrame
import io.circe.syntax._
import cats.implicits._

object EventStreamRoutes {

  def make[F[_]: Sync](eventSource: Stream[F, EventStreamEvent], eventPublish: Pipe[F, EventStreamEvent, Unit]): HttpRouter[F] =
    HttpRouter.make[F] { dsl =>
      import dsl._

      def toMessage[A: Encoder]: Pipe[F, A, WebSocketFrame] = _.map { event =>
        WebSocketFrame.Text(event.asJson.noSpaces)
      }

      HttpRoutes.of {
        case POST -> Root / "send" => Stream.emit(EventStreamEvent.ActivityLogged("demo")).through(eventPublish).compile.drain *> Created()
        case GET -> Root / "read" =>
          WebSocketBuilder[F].build(
            send = eventSource.through(toMessage),
            receive = _.drain
          )
      }
    }
}
