package io.runaid.eventstream

import io.circe.Codec

sealed trait EventStreamEvent extends Product with Serializable

object EventStreamEvent {
  case object Dummy extends EventStreamEvent
  final case class ActivityLogged(info: String) extends EventStreamEvent

  implicit val codec: Codec[EventStreamEvent] = io.circe.generic.semiauto.deriveCodec
}
