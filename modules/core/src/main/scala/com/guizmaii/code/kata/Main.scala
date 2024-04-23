package com.guizmaii.code.kata

import com.guizmaii.code.kata.types.*

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import scala.collection.mutable

trait RateLimiter {
  def rateLimit(clock: Clock)(customerId: String): Boolean
}

final class RateLimiterLive(maxRequestPerSecond: Int) extends RateLimiter {
  assert(maxRequestPerSecond > 1, "maxRequestPerSecond should be greater than 1")

  // customerId -> (lastUpdate, numberOfRequest)
  //
  // (lastUpdate, numberOfRequest) = get(customerId)
  //
  //  0s -> 1
  //  0s -> 2
  //  2s -> 3 // invalid
  //  2s -> 1 // valid

  val map = mutable.Map.empty[String, (Instant, Long)]

  override def rateLimit(clock: Clock)(customerId: String): Boolean =
    map.get(customerId) match { // effectively constant
      case None                        =>
        map += (customerId -> (Instant.now(clock) -> 1)) // effectively constant
        false
      case Some((lastUpdate, counter)) =>
        // -------|--------------------|-------
        // -------|t0------------------|t1------
        //   p0          p1               p2

        // p0: probably invalid state. Should never happen
        // p1: increment counter
        // p2: reset counter

        val t1         = lastUpdate.plus(1, ChronoUnit.SECONDS)
        val p          = Instant.now(clock)
        val isOutdated = p.isAfter(t1)
        if (isOutdated) {
          map += (customerId -> (Instant.now(clock) -> 1))
          false
        } else {
          val newCounter = counter + 1L
          map += (customerId -> (lastUpdate -> newCounter))
          newCounter > maxRequestPerSecond
        }
    }
}

object Main extends App {

  val name = Name("world")
  println(s"Hello, $name!")

}
