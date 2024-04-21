package com.guizmaii.code.kata.stubs

import com.guizmaii.code.kata.clients.IpifyClient
import com.guizmaii.code.kata.types.IpAddress

/**
 * Must stay un-implemented
 */
trait IpifyClientStub extends IpifyClient {
  override def fetchMyPublicIP: zio.Task[IpAddress] = ???
}
