import Libraries.*
import SbtTpolecatExtensions.*
import _root_.org.typelevel.sbt.tpolecat.TpolecatPlugin.autoImport.*
import org.typelevel.scalacoptions.ScalacOptions
import sbt.*
import sbt.Keys.*

object BuildHelper {

  private val javaTarget = "21"

  def env(v: String): Option[String] = sys.env.get(v)
  def unsafeEnv(v: String): String   = sys.env(v)

  lazy val stdSettings =
    noDoc ++ releaseModeScalacOptions ++ Seq(
      addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.2" cross CrossVersion.full),
      addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
      addCompilerPlugin("com.hmemcpy"   %% "zio-clippy"         % "0.0.5"),
      scalacOptions += "-P:clippy:show-original-error",                         // See https://github.com/hmemcpy/zio-clippy#additional-configuration
      scalacOptions -= "-Xlint:infer-any",                                      // Disable "a type was inferred to be `Any`" check which doesn't work well
      javacOptions ++= Seq("-source", javaTarget, "-target", javaTarget),
      scalacOptions ++= Seq("-Ymacro-annotations", "-Xsource:3", s"-release:$javaTarget"),
      // https://www.scala-lang.org/api/2.13.5/scala/annotation/elidable.html
      scalacOptions ++= (if (insideCI.value) Seq("-Xelide-below", scala.annotation.elidable.INFO.toString) else Nil),
      scalacOptions --= (if (insideCI.value) Nil else Seq("-Xfatal-warnings")), // enforced by the pre-push hook too
      // format: off
      tpolecatScalacOptions ++= Set(
        ScalacOptions.privateBackendParallelism(), // See https://github.com/typelevel/sbt-tpolecat/blob/main/plugin/src/main/scala/io/github/davidgregory084/ScalacOptions.scala#L409-L424
      ),
      // format: on
      libraryDependencies ++= Seq(zio, prelude, zioLogging) ++ tests.map(_ % Test),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
      excludeDependencies := Seq(ExclusionRule("log4j", "log4j")),
    )

  private lazy val releaseModeScalacOptions = Seq(
    tpolecatReleaseModeOptions ++= codeOptimizerConfig,
    tpolecatReleaseModeOptions += noOptimizerWarnings, // We don't want to emit errors on Optimizer's warnings
  )

  lazy val noDoc = Seq(
    (Compile / doc / sources)                := Seq.empty,
    (Compile / packageDoc / publishArtifact) := false,
  )

  /**
   * Copied from Cats
   */
  lazy val noPublishSettings = Seq(
    publish         := {},
    publishLocal    := {},
    publishM2       := {},
    publishArtifact := false,
  )

}
