package number

trait LocalService {

  def isLocalCountry(country: Country): Boolean
  def isLocalCity(city: City): Boolean

  val internationalCode: String
  val stdCode: String
}
