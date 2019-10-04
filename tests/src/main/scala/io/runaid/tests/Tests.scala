package io.runaid.tests

import cats.effect.{ExitCode, IO, IOApp}
import flawless.data.neu.{Suite, SuiteClass, TestApp}
import flawless._
import cats.implicits._

object Tests extends IOApp with TestApp {
  override def run(args: List[String]): IO[ExitCode] = runTests(args) {
    RunaidTests.runSuite.toSuites[IO]
  }
}

object RunaidTests extends SuiteClass[Nothing] {
  override val runSuite: Suite[Nothing] = {
    import flawless.data.neu.dsl._
    import flawless.data.neu.predicates.all._

    suite("RunaidTests") {
      tests(pureTest("first")(ensure(1, equalTo(1))))
    }
  }
}
