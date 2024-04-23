package com.guizmaii.code.kata

import zio.Scope
import zio.test.*

object MainSpec extends ZIOSpecDefault {

  private val truthinessSpec =
    suite("truthiness")(
      test("truthiness")(assertTrue(true))
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Main")(
      truthinessSpec
    )
}
