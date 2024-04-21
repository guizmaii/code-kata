package com.guizmaii.code.kata.stubs

import com.guizmaii.code.kata.clients.IpifyClient

/**
 * Must stay un-implemented
 */
trait IpifyClientStub extends IpifyClient {
  override def fetchMyPublicIP: zio.Task[String] = ???
}
