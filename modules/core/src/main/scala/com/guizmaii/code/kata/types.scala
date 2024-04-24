package com.guizmaii.code.kata

import zio.prelude.Subtype

object types {

  type Name = Name.Type
  object Name extends Subtype[String]

  type CourtId = CourtId.Type
  object CourtId extends Subtype[Int]

}
