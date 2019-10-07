package io.runaid.tests

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import flawless.TestApp
import flawless.SuiteClass
import flawless.data.Suite
import flawless.dsl._
import flawless.predicates.all._
import cats.implicits._
import flawless._

object Tests extends IOApp with TestApp {
  override def run(args: List[String]): IO[ExitCode] = runTests(args) {
    RunaidTests.runSuite.toSuites[IO]
  }
}

object RunaidTests extends SuiteClass[Nothing] {
  override val runSuite: Suite[Nothing] = {

    suite("RunaidTests") {
      tests(pureTest("first")(ensure(1, equalTo(1))))
    }
  }
}
