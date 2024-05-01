package com.guizmaii.code.kata

import zio.Scope
import zio.test.*

object MainSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Main")(
      test("truthiness")(assertTrue(true))
    )
}
