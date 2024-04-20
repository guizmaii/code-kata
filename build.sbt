import BuildHelper.*

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / envFileName                := ".env"
ThisBuild / organization               := "com.guizmaii"
ThisBuild / version                    := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion               := "2.13.13"
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

// ### App Modules ###

lazy val root =
  Project(id = "code-kata", base = file("."))
    .disablePlugins(RevolverPlugin)
    .settings(noDoc*)
    .settings(noPublishSettings*)
    .aggregate(
      main
    )

lazy val main =
  (project in file("modules/main"))
    .settings(name := "main")
    .settings(stdSettings*)
    .settings(libraryDependencies ++= Seq())
