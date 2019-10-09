package io.runaid.eventstream

import fs2.Pipe
import fs2.Stream
import io.circe.Encoder
import io.circe.syntax._
import io.runaid.http.HttpRouter
import org.http4s.HttpRoutes
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import cats.implicits._
import fs2.concurrent.SignallingRef
import cats.effect.Concurrent

object EventStreamRoutes {

  def make[F[_]: Concurrent](eventSource: Stream[F, EventStreamEvent]): F[HttpRouter[F]] = SignallingRef[F, Int](0).map { connectedCount =>
    val _ = eventSource

    HttpRouter.make[F] { dsl =>
      import dsl._

      def toMessage[A: Encoder]: Pipe[F, A, WebSocketFrame] = _.map { event =>
        WebSocketFrame.Text(event.asJson.noSpaces)
      }

      val counter = Stream.bracket(connectedCount.update(_ + 1))(_ => connectedCount.update(_ - 1))

      HttpRoutes.of {
        case GET -> Root / "read" =>
          WebSocketBuilder[F].build(
            send = counter *> connectedCount.discrete.through(toMessage),
            receive = _.drain
          )
      }
    }
  }
}
