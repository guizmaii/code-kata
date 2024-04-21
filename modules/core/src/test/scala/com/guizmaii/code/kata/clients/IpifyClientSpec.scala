package com.guizmaii.code.kata.clients

import sttp.client3.httpclient.zio.HttpClientZioBackend
import zio.test.*
import zio.test.Assertion.*
import zio.{Scope, Task, ZIO, ZLayer}

object IpifyClientSpec extends ZIOSpecDefault {

  private val fetchMyPublicIPSpec =
    suite("fetchMyPublicIP")(
      test("truthiness")(assertTrue(true)),
      test("fails if the IPify API is down") {
        val testingBackend =
          ZLayer.succeed {
            HttpClientZioBackend.stub.whenAnyRequest.thenRespondServerError()
          }

        val result: Task[String] =
          ZIO
            .serviceWithZIO[IpifyClient](_.fetchMyPublicIP)
            .provide(testingBackend, IpifyClient.live)

        assertZIO(result.exit)(fails(anything))
      },
      test("fails when API returns incorrectly formatted response") {
        val testingBackend =
          ZLayer.succeed {
            HttpClientZioBackend.stub.whenAnyRequest
              .thenRespond(s"""{"toto":1}""")
          }

        val result: Task[String] =
          ZIO
            .serviceWithZIO[IpifyClient](_.fetchMyPublicIP)
            .provide(testingBackend, IpifyClient.live)

        assertZIO(result.exit)(fails(anything))
      },
      test("succeeds when API returns a valid response") {
        val expectedIp     = "139.216.233.78"
        val testingBackend =
          ZLayer.succeed {
            HttpClientZioBackend.stub.whenAnyRequest
              .thenRespond(s"""{"ip":"$expectedIp"}""")
          }

        val result: Task[String] =
          ZIO
            .serviceWithZIO[IpifyClient](_.fetchMyPublicIP)
            .provide(testingBackend, IpifyClient.live)

        assertZIO(result)(equalTo(expectedIp))
      },
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("IpifyClient")(
      fetchMyPublicIPSpec
    ).provide(zio.Runtime.removeDefaultLoggers) // disable noisy logs in tests
}
