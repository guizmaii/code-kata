package com.guizmaii.code.kata

import zio.Scope
import zio.test.*
import zio.test.Assertion.*

import java.time.{Clock, Duration, Instant, ZoneId}

object MainSpec extends ZIOSpecDefault {

  private val rateLimitSpec =
    suite("rateLimit")(
      test("test all the scenarios") {
        val customerId  = "toto"
        val clock       = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val rateLimiter = new RateLimiterLive(3)

        // scenario 1
        // We don't know the user, the result should be false
        val scenario1Result = rateLimiter.rateLimit(clock)(customerId)

        // scenario 2
        // We KNOW the user but we haven't pass the limit, the result should be false
        val scenario2Result = rateLimiter.rateLimit(clock)(customerId)

        // scenario 3
        // We KNOW the user but we are at the limit, not passed it yet, the result should be false
        val scenario3Result = rateLimiter.rateLimit(clock)(customerId)

        // scenario 4
        // We KNOW the user and we passed the limit, the result should be true
        val scenario4Result = rateLimiter.rateLimit(clock)(customerId)

        // scenario 5
        // The previous counter should be outdated so the counter should be reset to 1, so the result should be `false`
        val newClock: Clock = Clock.offset(clock, Duration.ofSeconds(10))
        val scenario5Result = rateLimiter.rateLimit(newClock)(customerId)

        assert(scenario1Result)(isFalse) &&
        assert(scenario2Result)(isFalse) &&
        assert(scenario3Result)(isFalse) &&
        assert(scenario4Result)(isTrue) &&
        assert(scenario5Result)(isFalse)
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Main")(
      rateLimitSpec
    )
}
