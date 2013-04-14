package uk.co.unclealex.callerid.remote.numbers

import com.google.common.base.Optional

/**
 * An interface for classes that can turn a telephone number in string form into a normalised phone number.
 */
public interface NumberLocationService {

    /**
     * Decompose a telephone number into a normalised {@link PhoneNumber}.
     */
	def Optional<PhoneNumber> decompose(String number);

}
