package com.guizmaii.code.kata

import zio.prelude.Subtype

object types {
  import com.guizmaii.code.kata.extensions.TypesExtensions.*

  type IpAddress = IpAddress.Type
  object IpAddress extends Subtype[String] with Extended[String]

}
