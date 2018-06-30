package number

/**
  * A service used to determine whether a country and/or a city are local to the land-line.
  */
trait LocalService {

  /**
    * Check to see if a country is local.
    * @param country The country to check.
    * @return True if the country is local, false otherwise.
    */
  def isLocalCountry(country: Country): Boolean

  /**
    * Check to see if a city is local.
    * @param city The city to check.
    * @return True if the city is local, false otherwise.
    */
  def isLocalCity(city: City): Boolean

  /**
    * The international code for the land-line.
    */
  val internationalCode: String

  /**
    * The STD code for the land-line.
    */
  val stdCode: String
}
