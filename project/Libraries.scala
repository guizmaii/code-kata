import sbt.*

object Libraries {

  val zioVersion       = "2.0.22"
  val slf4jVersion     = "2.0.9"
  val zioConfigVersion = "3.0.7"
  val zioJsonVersion   = "0.6.2"

  val zio        = "dev.zio"       %% "zio"                % zioVersion
  val prelude    = "dev.zio"       %% "zio-prelude"        % "1.0.0-RC21"
  val zioLogging = "dev.zio"       %% "zio-logging-slf4j2" % "2.1.17"
  val logback    = "ch.qos.logback" % "logback-classic"    % "1.5.6"

  val zioJson = Seq(
    "dev.zio" %% "zio-json"        % zioJsonVersion,
    "dev.zio" %% "zio-json-macros" % zioJsonVersion,
  )

  val zioConfig = Seq(
    "dev.zio" %% "zio-config"          % zioConfigVersion,
    "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
    "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  )

  val tests = Seq(
    "dev.zio" %% "zio-test"          % zioVersion,
    "dev.zio" %% "zio-test-sbt"      % zioVersion,
    "dev.zio" %% "zio-test-magnolia" % zioVersion,
    "dev.zio" %% "zio-mock"          % "1.0.0-RC11",
  )

  val loggingRuntime = Seq(
    logback,
    "net.logstash.logback" % "logstash-logback-encoder" % "7.4",
    "org.slf4j"            % "jul-to-slf4j"             % slf4jVersion,
    "org.slf4j"            % "log4j-over-slf4j"         % slf4jVersion,
    "org.slf4j"            % "jcl-over-slf4j"           % slf4jVersion,
    "org.slf4j"            % "slf4j-api"                % slf4jVersion,
  )

}
