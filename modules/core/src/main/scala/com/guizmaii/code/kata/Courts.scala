package com.guizmaii.code.kata

import com.guizmaii.code.kata.types.CourtId

import scala.collection.mutable

/**
 * Implement a function that given a list of tennis court bookings with start and finish times,
 * returns a plan assigning each booking to a specific court,
 * ensuring each court is used by only one booking at a time.
 *
 * An example of the booking record might look like:
 * class BookingRecord:
 * id: int // ID of the person making the booking.
 * start_time: int
 * finish_time: int
 * and our function is going to look like:
 * List<Court> assignCourts(List<BookingRecord> bookingRecords)
 */
object Courts {

  final case class BookingRecord(bookingId: Int, startTime: Int, finishTime: Int)
  final case class Court(courtId: CourtId, bookings: List[BookingRecord])

  // a: |-----|  -- court 0
  // b:    |-----|   -- court 1
  //           |-----|   -- court 0
  //                   |-----|  -- court 0
  def doOverlap(a: BookingRecord, b: BookingRecord): Boolean =
    b.startTime < a.finishTime && a.startTime < b.startTime

  def assignCourts(bookingRecords: List[BookingRecord]): List[Court] =
    bookingRecords match {
      case Nil         => List.empty                                               // 0 elements in the list
      case head :: Nil => List(Court(courtId = CourtId(0), bookings = List(head))) // 1 elements in the list
      case bookings    =>                                                          // more than 1 element in the list
        var courtCounter: CourtId = CourtId(0)

        def nextCourtId: CourtId = {
          val newCourt = CourtId(courtCounter + 1)
          courtCounter = newCourt
          newCourt
        }

        val result: mutable.Map[CourtId, List[BookingRecord]] = mutable.Map.empty[CourtId, List[BookingRecord]]

        def findCourt(booking: BookingRecord): Option[CourtId] =
          result
            .find { case (_, bookings) =>
              val lastBooking = bookings.head
              !doOverlap(booking, lastBooking)
            }
            .map(_._1)

        val sortedBookings = bookings.sortBy(_.startTime)
        val firstBooking   = sortedBookings.head
        result += (courtCounter -> List(firstBooking))

        sortedBookings.tail.map { booking =>
          findCourt(booking) match {
            case Some(courtId) => result += (courtId     -> booking :: result(courtId))
            case None          => result += (nextCourtId -> List(booking))
          }
        }

        result.map { case (courtId, bookings) => Court(courtId = courtId, bookings = bookings.reverse) }.toList
    }
}
