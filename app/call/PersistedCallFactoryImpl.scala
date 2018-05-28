package call

import number.{NumberFormatter, PhoneNumber}

class PersistedCallFactoryImpl(numberFormatter: NumberFormatter) extends PersistedCallFactory {

  def persistPhoneNumber(number: PhoneNumber): PersistedPhoneNumber = {
    val formattedNumber = numberFormatter.formatNumberAsInternational(number).default
    PersistedPhoneNumber(number.normalisedNumber, formattedNumber, number.city.map(_.name), number.countries.map(_.name))
  }

  override def persist(call: Call): PersistedCall = {
    val persistedCaller: PersistedCaller = call.caller match {
      case Withheld => PersistedWithheld
      case Known(name, phoneType, phoneNumber) => PersistedKnown(name, phoneType, persistPhoneNumber(phoneNumber))
      case Unknown(phoneNumber) => PersistedUnknown(persistPhoneNumber(phoneNumber))
      case Undefinable(str) => PersistedUndefinable(str)
    }
    PersistedCall(call.when, persistedCaller)
  }
}
