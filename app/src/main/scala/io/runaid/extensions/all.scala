package io.runaid.extensions

import cats.Applicative
import cats.effect.Resource

object all {
  implicit final class LiftResourceExtension[F[_], A](private val fa: F[A]) extends AnyVal {
    def liftResource(implicit F: Applicative[F]): Resource[F, A] = Resource.liftF(fa)
  }
}
