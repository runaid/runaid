package io.runaid.eventstream

import io.circe.Codec

sealed trait EventStreamEvent extends Product with Serializable

object EventStreamEvent {
  case object Dummy extends EventStreamEvent
  final case class ActivityLogged(log: ActivityLog) extends EventStreamEvent

  implicit val codec: Codec[EventStreamEvent] = io.circe.generic.semiauto.deriveCodec
}

final case class ActivityLog(lat: Long, long: Long)

object ActivityLog {
  implicit val codec: Codec[ActivityLog] = io.circe.generic.semiauto.deriveCodec
}
