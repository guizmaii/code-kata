package com.guizmaii.code.kata

import zio.Scope
import zio.test.*

object CliSpec extends ZIOSpecDefault {

  private val printMyPublicIPSpec =
    suite("printMyPublicIP")(
      test("truthiness")(assertTrue(true))
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Cli")(
      printMyPublicIPSpec
    ).provide(zio.Runtime.removeDefaultLoggers) // disable noisy logs in tests
}
