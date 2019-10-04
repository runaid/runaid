package io.runaid.eventstream

import cats.effect.Sync
import fs2.Pipe
import fs2.Stream
import io.circe.Encoder
import io.circe.syntax._
import io.runaid.http.HttpRouter
import org.http4s.HttpRoutes
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import cats.implicits._

object EventStreamRoutes {

  def make[F[_]: Sync](eventSource: Stream[F, EventStreamEvent]): HttpRouter[F] =
    HttpRouter.make[F] { dsl =>
      import dsl._

      def toMessage[A: Encoder]: Pipe[F, A, WebSocketFrame] = _.map { event =>
        WebSocketFrame.Text(event.asJson.noSpaces)
      }

      val activitiesOnly: Pipe[F, EventStreamEvent, ActivityLog] = _.collect {
        case EventStreamEvent.ActivityLogged(log) => log
      }

      val distance: (ActivityLog, ActivityLog) => Long = (a, b) => {
        def dist(a: Long, b: Long): Double = Math.sqrt(((a * a) + (b * b)).toDouble)

        dist(
          b.long - a.long,
          b.lat - a.lat
        ).round
      }

      val foldAsDistance: Pipe[F, ActivityLog, Long] =
        _.zipWithPrevious.scanMap(_.leftSequence.map(distance.tupled)).unNone

      HttpRoutes.of {
        case GET -> Root / "read" =>
          WebSocketBuilder[F].build(
            send = eventSource.through(activitiesOnly).through(foldAsDistance).through(toMessage),
            receive = _.drain
          )
      }
    }
}
