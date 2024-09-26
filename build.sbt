import BuildHelper.*
import Libraries.*

Global / onChangedBuildSource := ReloadOnSourceChanges

def cliVersion = sys.env.getOrElse("CLI_VERSION", "0.0.1-SNAPSHOT")

ThisBuild / envFileName                := ".env"
ThisBuild / organization               := "com.guizmaii"
ThisBuild / version                    := cliVersion
ThisBuild / scalaVersion               := "2.13.15"
ThisBuild / scalafmtCheck              := true
ThisBuild / scalafmtSbtCheck           := true
ThisBuild / scalafmtOnCompile          := !insideCI.value
ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbOptions += "-P:semanticdb:synthetics:on"
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision // use Scalafix compatible version
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)
ThisBuild / scalafixDependencies ++= List(
  "com.github.vovapolu"                      %% "scaluzzi" % "0.1.23",
  "io.github.ghostbuster91.scalafix-unified" %% "unified"  % "0.0.9",
)

// ### Aliases ###

addCommandAlias("tc", "Test/compile")
addCommandAlias("ctc", "clean; Test/compile")
addCommandAlias("rctc", "reload; clean; Test/compile")

addCommandAlias("kata", "core/run")

// ### App Modules ###

lazy val root =
  Project(id = "code-kata", base = file("."))
    .disablePlugins(RevolverPlugin)
    .settings(noDoc *)
    .settings(noPublishSettings *)
    .aggregate(
      core
    )

lazy val core =
  (project in file("modules/core"))
    .enablePlugins(BuildInfoPlugin)
    .enablePlugins(NativeImagePlugin)
    .settings(name := "core")
    .settings(stdSettings *)
    .settings(libraryDependencies ++= Seq(cli, zioJson) ++ sttp)
    .settings(
      // BuildInfo settings
      buildInfoKeys    := Seq[BuildInfoKey](BuildInfoKey.action("version")(cliVersion)),
      buildInfoPackage := "com.guizmaii.code.kata",
      buildInfoObject  := "BuildInfo",
    )
    .settings(
      // sbt-native-image configs
      Compile / mainClass  := Some("com.guizmaii.code.kata.Main"),
      nativeImageVersion   := "21",
      nativeImageJvm       := "graalvm-oracle", // See also https://github.com/coursier/jvm-index/pull/210#issuecomment-1637703847
      nativeImageOptions += "--no-fallback",
      nativeImageInstalled := insideCI.value,
      nativeImageOutput    := (ThisBuild / baseDirectory).value / "kata",
      Global / excludeLintKeys ++= Set(nativeImageVersion, nativeImageJvm), // Wrongly reported as unused keys by sbt
    )
