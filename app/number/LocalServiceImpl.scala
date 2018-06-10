package number

class LocalServiceImpl(val internationalCode : String, val stdCode : String) extends LocalService {
  override def isLocalCountry(country: Country): Boolean = country.internationalDiallingCode == internationalCode

  override def isLocalCity(city: City): Boolean = city.stdCode == stdCode
}
