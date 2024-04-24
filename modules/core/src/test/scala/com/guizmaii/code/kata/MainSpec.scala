package com.guizmaii.code.kata

import com.guizmaii.code.kata.Courts.*
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

  private val doOverlapSpec =
    suite("doOverlap")(
      test("a is strictly before b") {
        val a      = BookingRecord(0, 0, 3)
        val b      = BookingRecord(1, 5, 6)
        val result = doOverlap(a, b)

        assert(result)(isFalse)
      },
      test("a is finishing inside b") {
        val a      = BookingRecord(0, 0, 3)
        val b      = BookingRecord(1, 2, 5)
        val result = doOverlap(a, b)

        assert(result)(isTrue)
      },
      test("a is finishing at the same time b starts") {
        val a      = BookingRecord(0, 0, 3)
        val b      = BookingRecord(1, 3, 5)
        val result = doOverlap(a, b)

        assert(result)(isFalse)
      },
    )

  private val assignCourtsSpec =
    suite("assignCourts")(
      test("truth")(assert(true)(isTrue)),
      test("given 0 bookings, we must return 0 court") {
        ???
      },
      test("given 1 booking, we must return 1 court") {
        ???
      },
      test("given 2 overlaping bookings, we must returns 2 courts") {
        ???
      },
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Main")(
      rateLimitSpec,
      assignCourtsSpec,
      doOverlapSpec,
    )
}
