package uk.co.unclealex.callerid.phonenumber.model;

import com.google.common.base.Function;

/**
 * A phone number represents all discoverable information about a phone number.
 * This includes and is limited to:
 * <ul>
 * <li>the number that was called,</li>
 * <li>the countries that the phone number may have originated from (as some
 * countries share international codes, non-geographic calls from those regions
 * cannot be tied down to a particular country) and</li>
 * <li>the town or city where the call came from.</li>
 * </ul>
 * 
 * The possible interactions between the different parts of information are all
 * encapsulated by classes that implement this interface.
 * 
 * @author alex
 * 
 */
public interface PhoneNumber {

  /**
   * Accept a {@link Visitor}.
   * 
   * @param visitor
   *          The visitor to accept.
   * @return The value returned by the visitor.
   */
  public <T> T accept(Visitor<T> visitor);

  /**
   * A visitor interface for the different kinds of {@link PhoneNumber}s.
   * 
   * @author alex
   * 
   * @param <T>
   *          The type of value each method should return.
   */
  public interface Visitor<T> {

    /**
     * Visit a non-specific {@link PhoneNumber}.
     * 
     * @param phoneNumber
     *          The {@link PhoneNumber} to visit.
     * @return To be defined by implentations.
     */
    T visit(PhoneNumber phoneNumber);

    /**
     * Visit a {@link NumberOnlyPhoneNumber}.
     * 
     * @param numberOnlyPhoneNumber
     *          The {@link NumberOnlyPhoneNumber} to visit.
     * @return To be defined by implentations.
     */
    T visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber);

    /**
     * Visit a {@link CountriesOnlyPhoneNumber}.
     * 
     * @param countriesOnlyPhoneNumber
     *          The {@link CountriesOnlyPhoneNumber} to visit.
     * @return To be defined by implentations.
     */
    T visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber);

    /**
     * Visit a {@link CountryAndAreaPhoneNumber}.
     * 
     * @param countryAndAreaPhoneNumber
     *          The {@link CountryAndAreaPhoneNumber} to visit.
     * @return To be defined by implentations.
     */
    T visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber);

    /**
     * Visit a {@link CountryAndAreaPhoneNumber}.
     * 
     * @param countryAndAreaPhoneNumber
     *          The {@link CountryAndAreaPhoneNumber} to visit.
     * @return To be defined by implentations but defaults to <code>null</code>.
     */
    T visit(WithheldPhoneNumber withheldPhoneNumber);

    /**
     * A default implementation of {@link Visitor} that throws an
     * {@link IllegalArgumentException} when visiting a non-specific
     * {@link PhoneNumber} implementation.
     * 
     * @author alex
     * 
     * @param <T>
     */
    public abstract static class Default<T> implements Visitor<T> {

      /**
       * {@inheritDoc}
       */
      public T visit(PhoneNumber phoneNumber) {
        throw new IllegalStateException("The type " + phoneNumber.getClass() + " is not a known phone number type.");
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public T visit(WithheldPhoneNumber withheldPhoneNumber) {
        return null;
      }

      /**
       * Convert this visitor into a {@link Function}.
       * 
       * @return A {@link Function} that returns the value of this visitor for
       *         its supplied {@link PhoneNumber}.
       */
      public Function<PhoneNumber, T> asFunction() {
        Function<PhoneNumber, T> f = new Function<PhoneNumber, T>() {
          public T apply(PhoneNumber phoneNumber) {
            return phoneNumber.accept(Default.this);
          }
        };
        return f;
      }
    }
  }

}
