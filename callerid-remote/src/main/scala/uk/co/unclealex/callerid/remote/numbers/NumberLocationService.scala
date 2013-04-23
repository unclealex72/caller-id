package uk.co.unclealex.callerid.remote.numbers

import com.google.common.base.Optional

/**
 * An interface for classes that can turn a telephone number in string form into a normalised phone number.
 */
trait NumberLocationService {

  /**
   * Decompose a telephone number into a normalised {@link PhoneNumber}.
   */
  def decompose(number: String): PhoneNumber

}
