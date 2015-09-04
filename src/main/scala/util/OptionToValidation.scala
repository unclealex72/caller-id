package util

import scalaz.{Failure, Success, Validation, ValidationNel}

/**
 * Created by alex on 02/09/15.
 */
object OptionToValidation {

  implicit class OptionToValidation[A](o: Option[A]) {

    def ~~(msg: String): ValidationNel[String, A] = this.~(msg).toValidationNel

    def ~(msg: String): Validation[String, A] = o match {
      case Some(a) => Success(a)
      case None => Failure(msg)
    }
  }

}
