/**
 * Created by alex on 12/09/15.
 */
package object contact {

  type ContactName = String
  type PhoneNumber = String
  type PhoneType = Option[String]
  type Phone = (PhoneNumber, PhoneType)
}
