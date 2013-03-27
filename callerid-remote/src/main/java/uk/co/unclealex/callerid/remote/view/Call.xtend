package uk.co.unclealex.callerid.remote.view

import org.codehaus.jackson.annotate.JsonProperty
import org.codehaus.jackson.annotate.JsonCreator

/**
 * A call is a flat representation of all known details about a call record. This can then be interrogated
 * and displayed by a client.
 */
 @Data class Call {
   
   /**
    * The ISO 8601 date and time this call was received.
    */
   val String callReceivedTime;
   
   /**
    * The international prefix of the telephone number that made this call.
    */
   val String internationalPrefix;
   
   /**
    * The STD code of the telephone number that made this call or null if the number was non-geographic.
    */
   val String stdCode;
   
   /**
    * The non-geographical part of the telephone number that made this call.
    */
   val String number;
   
   /**
    * The country from where this call originated.
    */
   val String country;
   
   /**
    * The geographical city where this call was made or null if the number was non-geographic.
    */
   val String city;
   
   /**
    * The name of the contact who phoned, if known.
    */
   val String contactName;
   
   /**
    * The address of the contact who phoned, if known.
    */
   val String contactAddress;

  @JsonCreator
  new(@JsonProperty("callReceivedTime") String callReceivedTime, 
      @JsonProperty("internationalPrefix") String internationalPrefix, 
      @JsonProperty("stdCode") String stdCode,
      @JsonProperty("number") String number, 
      @JsonProperty("country") String country, 
      @JsonProperty("city") String city, 
      @JsonProperty("contactName") String contactName, 
      @JsonProperty("contactAddress") String contactAddress
  ) {
    super();
    this._callReceivedTime = callReceivedTime;
    this._internationalPrefix = internationalPrefix;
    this._stdCode = stdCode;
    this._number = number;
    this._country = country;
    this._city = city;
    this._contactName = contactName;
    this._contactAddress = contactAddress;
  }
   
}