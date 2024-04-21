package com.guizmaii.code.kata

import com.guizmaii.code.kata.clients.IpifyClient
import com.guizmaii.code.kata.extensions.SttpZioLogger
import sttp.client3.SttpBackend
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3.logging.{DefaultLog, LogLevel, LoggingBackend}
import zio.cli.*
import zio.logging.backend.JPL
import zio.{Executor, Runtime, Scope, Task, TaskLayer, ZIOAppArgs, ZLayer}

/**
 * Usage:
 *
 * {{{
 * ./kata my-ip
 * }}}
 */
object Main extends ZIOCliDefault {

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    (Runtime.removeDefaultLoggers >>> JPL.jpl) ++
      Runtime.setExecutor(Executor.makeDefault(autoBlocking = false))

  sealed trait Subcommand extends Product with Serializable
  object Subcommand {
    type Help = Help.type
    final case object Help extends Subcommand

    final case class GetMyIP(verbose: Boolean) extends Subcommand
  }
  import Subcommand.*

  private val getMyIPCmd: Command[GetMyIP] =
    Command(name = "my-ip", options = Options.boolean("verbose").alias("v"), args = Args.none)
      .withHelp(HelpDoc.p("Fetch your public IP"))
      .map(verbose => Subcommand.GetMyIP(verbose))

  private val getHelp: Command[Help] =
    Command(name = "help", options = Options.none, args = Args.none)
      .withHelp(HelpDoc.p("Print commandline documentation"))
      .map(_ => Subcommand.Help)

  private val appCmd: Command[Subcommand] =
    Command("kata").subcommands(getHelp, getMyIPCmd)

  override val cliApp: CliApp[ZIOAppArgs & Scope, Any, Any] =
    CliApp.make(
      name = "kata",
      version = BuildInfo.version,
      summary = HelpDoc.Span.text("This is some documentation for the 'kata' CLI"),
      command = appCmd,
    ) {
      case Subcommand.Help             => cliApp.run(List.empty)
      case Subcommand.GetMyIP(verbose) =>
        Cli.printMyPublicIP
          .provide(
            IpifyClient.live,
            sttpClientLayer(verbose),
          )
          .logError("Error while running fetch public IP")
    }

  private def sttpClientLayer(verbose: Boolean): TaskLayer[SttpBackend[Task, Any]] =
    ZLayer.scoped {
      for {
        backend <- HttpClientZioBackend.scoped()
      } yield LoggingBackend(
        backend,
        SttpZioLogger,
        beforeRequestSendLogLevel = if (verbose) LogLevel.Info else LogLevel.Debug,
        responseLogLevel = code =>
          if (verbose) if (code.isClientError || code.isServerError) LogLevel.Warn else LogLevel.Info
          else DefaultLog.defaultResponseLogLevel(code),
      )
    }

}
