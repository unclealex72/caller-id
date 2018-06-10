package number

import cats.data.ValidatedNel

/**
 * An interface for classes that can turn a telephone number in string form into a normalised phone number.
 */
trait PhoneNumberFactory {

  /**
   * Decompose a telephone number into a normalised [[PhoneNumber]].
   */
  def decompose(number: String): ValidatedNel[String, PhoneNumber]

}
