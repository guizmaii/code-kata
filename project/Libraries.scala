import sbt.*

object Libraries {

  val zioVersion  = "2.0.22"
  val sttpVersion = "3.9.6"

  val zio        = "dev.zio" %% "zio"             % zioVersion
  val prelude    = "dev.zio" %% "zio-prelude"     % "1.0.0-RC25"
  val zioLogging = "dev.zio" %% "zio-logging-jpl" % "2.2.3"
  val cli        = "dev.zio" %% "zio-cli"         % "0.5.0"
  val zioJson    = "dev.zio" %% "zio-json"        % "0.6.2"

  val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "core"     % sttpVersion,
    "com.softwaremill.sttp.client3" %% "zio"      % sttpVersion,
    "com.softwaremill.sttp.client3" %% "zio-json" % sttpVersion,
  )

  val tests = Seq(
    "dev.zio" %% "zio-test"          % zioVersion,
    "dev.zio" %% "zio-test-sbt"      % zioVersion,
    "dev.zio" %% "zio-test-magnolia" % zioVersion,
    "dev.zio" %% "zio-mock"          % "1.0.0-RC12",
  )

}
