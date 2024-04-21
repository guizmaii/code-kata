package com.guizmaii.code.kata

import com.guizmaii.code.kata.clients.IpifyClient
import zio.{RIO, ZIO}

object Cli {

  val printMyPublicIP: RIO[IpifyClient, Unit] =
    for {
      client  <- ZIO.service[IpifyClient]
      console <- ZIO.console
      ip      <- client.fetchMyPublicIP
      _       <- console.printLine(s"Your public IP is: $ip")
    } yield ()

}
