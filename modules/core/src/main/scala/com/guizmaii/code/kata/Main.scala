package com.guizmaii.code.kata

import com.guizmaii.code.kata.clients.IpifyClient
import sttp.client3.SttpBackend
import sttp.client3.httpclient.zio.HttpClientZioBackend
import zio.cli.*
import zio.{Scope, Task, TaskLayer, ZIOAppArgs, ZLayer}

/**
 * Usage:
 *
 * {{{
 * ./kata my-ip
 * }}}
 */
object Main extends ZIOCliDefault {

  sealed trait Subcommand extends Product with Serializable
  object Subcommand {
    type GetMyIP = GetMyIP.type
    final case object GetMyIP extends Subcommand

    type Help = Help.type
    final case object Help extends Subcommand
  }
  import Subcommand.*

  private val getMyIPCmd: Command[GetMyIP] =
    Command(name = "my-ip", options = Options.none, args = Args.none)
      .withHelp(HelpDoc.p("Fetch your public IP"))
      .map(_ => Subcommand.GetMyIP)

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
      summary = HelpDoc.Span.text("This is some documentation for the 'kata' CLI app"),
      command = appCmd,
    ) {
      case Subcommand.Help    => cliApp.run(List.empty)
      case Subcommand.GetMyIP =>
        Cli.printMyPublicIP
          .provide(
            IpifyClient.live,
            sttpClientLayer,
          )
          .logError("Error while running fetch public IP")
    }

  private val sttpClientLayer: TaskLayer[SttpBackend[Task, Any]] =
    ZLayer.scoped(HttpClientZioBackend.scoped())

}
