package com.guizmaii.code.kata.extensions

import zio.json.{JsonDecoder, JsonEncoder}
import zio.prelude.Newtype

object TypesExtensions {

  trait Unsafe[A] {
    self: Newtype[A] =>
    final def unsafe(a: A): Type                 = wrap(a)
    final def unsafeAll[F[_]](fa: F[A]): F[Type] = fa.asInstanceOf[F[Type]]
  }

  trait Extended[A] extends Unsafe[A] {
    self: Newtype[A] =>

    implicit final def encoder(implicit encoder: JsonEncoder[A]): JsonEncoder[Type] = derive[JsonEncoder]
    implicit final def decoder(implicit encoder: JsonDecoder[A]): JsonDecoder[Type] = derive[JsonDecoder]
  }
}
