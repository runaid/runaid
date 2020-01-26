val CatsEffectVersion = "2.0.0"
val Fs2Version = "2.0.1"
val Http4sVersion = "0.21.0-M5"
val CirceVersion = "0.12.1"
val DoobieVersion = "0.8.4"
val FlywayVersion = "5.0.5"
val LogbackVersion = "1.2.3"
val ScalaTestVersion = "3.0.8"
val ScalaCheckVersion = "1.13.4"

val app = project.settings(
  organization := "io",
  name := "runaid",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.10",
  scalacOptions := Options.all,
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % CatsEffectVersion,
    "co.fs2" %% "fs2-io" % Fs2Version,
    "com.github.pureconfig" %% "pureconfig-generic" % "0.12.1",
    "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
    "org.http4s" %% "http4s-circe" % Http4sVersion,
    "org.http4s" %% "http4s-dsl" % Http4sVersion,
    "io.circe" %% "circe-core" % CirceVersion,
    "io.circe" %% "circe-generic" % CirceVersion,
    "org.flywaydb" % "flyway-core" % FlywayVersion,
    "org.tpolecat" %% "doobie-core" % DoobieVersion,
    "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
    "ch.qos.logback" % "logback-classic" % LogbackVersion,
    "io.scalaland" %% "chimney" % "0.3.2"
//      "org.scalatest"   %% "scalatest"           % ScalaTestVersion  % Test,
//      "org.scalacheck"  %% "scalacheck"          % ScalaCheckVersion % Test,
//      "org.tpolecat"    %% "doobie-scalatest"    % DoobieVersion % Test
  )
)

val tests = project
  .settings(
    libraryDependencies ++= Seq(
      "com.kubukoz" %% "flawless-core" % "0.1.0-M6"
    )
  )
  .dependsOn(app)

val root = project.in(file(".")).aggregate(app, tests)
