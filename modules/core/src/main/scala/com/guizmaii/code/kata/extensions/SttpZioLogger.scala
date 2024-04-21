package com.guizmaii.code.kata.extensions

import sttp.client3.logging.LogLevel
import zio.{Cause, Task, ZIO}

object SttpZioLogger extends sttp.client3.logging.Logger[Task] {

  override def apply(level: LogLevel, message: => String): Task[Unit] =
    level match {
      case LogLevel.Trace => ZIO.logTrace(message)
      case LogLevel.Debug => ZIO.logDebug(message)
      case LogLevel.Info  => ZIO.logInfo(message)
      case LogLevel.Warn  => ZIO.logWarning(message)
      case LogLevel.Error => ZIO.logError(message)
    }

  override def apply(level: LogLevel, message: => String, t: Throwable): Task[Unit] =
    level match {
      case LogLevel.Trace => ZIO.logTraceCause(message, Cause.fail(t))
      case LogLevel.Debug => ZIO.logDebugCause(message, Cause.fail(t))
      case LogLevel.Info  => ZIO.logInfoCause(message, Cause.fail(t))
      case LogLevel.Warn  => ZIO.logWarningCause(message, Cause.fail(t))
      case LogLevel.Error => ZIO.logErrorCause(message, Cause.fail(t))
    }
}
