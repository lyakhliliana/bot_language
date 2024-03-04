package utils

import cats.implicits._

object Errors {
  sealed abstract class BaseError(val message: String, val cause: Option[Throwable] = None)

  case class AppError(cause0: Throwable) extends BaseError("Internal error", cause0.some)
}
