package com.guizmaii.code.kata

import com.guizmaii.code.kata.stubs.IpifyClientStub
import com.guizmaii.code.kata.types.IpAddress
import zio.test.*
import zio.test.Assertion.*
import zio.{Scope, Task, ULayer, ZIO, ZLayer}

object CliSpec extends ZIOSpecDefault {

  private val printMyPublicIPSpec =
    suite("printMyPublicIP")(
      test("truthiness")(assertTrue(true)),
      test("fails if the IPify client call fails") {
        checkAll(Gen.boolean) { ipOnly =>
          val client: ULayer[IpifyClientStub] =
            ZLayer.succeed {
              new IpifyClientStub {
                override def fetchMyPublicIP: Task[IpAddress] = ZIO.fail(new RuntimeException("Boom!"))
              }
            }

          val result = Cli.printMyPublicIP(ipOnly).provideLayer(client)

          assertZIO(result.exit)(fails(anything))
        }
      },
      test("if the IPify client call succeeds, prints the receive public IP") {
        checkAll(Gen.boolean) { ipOnly =>
          val ip             = IpAddress("123")
          val expectedOutput = if (ipOnly) s"$ip\n" else s"Your public IP is: $ip\n"

          val client: ULayer[IpifyClientStub] =
            ZLayer.succeed {
              new IpifyClientStub {
                override def fetchMyPublicIP: Task[IpAddress] = ZIO.succeed(ip)
              }
            }

          val result = Cli.printMyPublicIP(ipOnly).provideLayer(client)

          assertZIO(result)(isUnit) && assertZIO(TestConsole.output)(equalTo(Vector(expectedOutput)))
        }
      },
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Cli")(
      printMyPublicIPSpec
    ).provide(zio.Runtime.removeDefaultLoggers) // disable noisy logs in tests
}
