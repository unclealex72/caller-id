package uk.co.unclealex.callerid.phonenumber.service;

import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;

/**
 * An interface for classes that can create a {@link PhoneNumber} from a string.
 * International phone numbers always start with 00 and national phone numbers
 * always start with a 0.
 * 
 * @author alex
 * 
 */
public interface PhoneNumberFactory {

  /**
   * Create a new {@link PhoneNumber} from the supplied string.
   * @param number The number that is calling.
   * @return A new {@link PhoneNumber} that represents the number that called.
   */
  public PhoneNumber create(String number);
}
