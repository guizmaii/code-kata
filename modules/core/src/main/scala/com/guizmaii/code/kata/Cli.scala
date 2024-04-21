package com.guizmaii.code.kata

import com.guizmaii.code.kata.clients.IpifyClient
import zio.{RIO, ZIO}

object Cli {

  def printMyPublicIP(ipOnly: Boolean): RIO[IpifyClient, Unit] =
    for {
      client  <- ZIO.service[IpifyClient]
      console <- ZIO.console
      ip      <- client.fetchMyPublicIP
      result   = if (ipOnly) ip else s"Your public IP is: $ip"
      _       <- console.printLine(result)
    } yield ()

}
