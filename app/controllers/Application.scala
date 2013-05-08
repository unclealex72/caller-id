package controllers

import play.api._
import play.api.mvc._
import uk.co.unclealex.callerid.remote.number.City
import scalaz.NonEmptyList
import uk.co.unclealex.callerid.remote.number.Country
import uk.co.unclealex.callerid.remote.call.ReceivedCall
import java.util.Date
import uk.co.unclealex.callerid.remote.number.PhoneNumber
import uk.co.unclealex.callerid.remote.contact.Contact
import uk.co.unclealex.callerid.remote.number.LocationConfiguration
import uk.co.unclealex.callerid.remote.number.NumberFormatter
import uk.co.unclealex.callerid.remote.number.NumberFormatterImpl
import model.CallModel

object Application extends Controller {

  val uk = NonEmptyList(Country("United Kingdom", "44", "gb", List()))
  val us = NonEmptyList(Country("United States of America", "1", "us", List()))
  val basingstoke = Some(City("Basingstoke", "1256"))
  val guildford = Some(City("Guildford", "1483"))
  val france = NonEmptyList(Country("France", "33", "fr", List()))

  val call1 = ReceivedCall(
    at("2012-09-05T09:12T+01:00"),
    PhoneNumber("+441256362362", uk, basingstoke, "362362"),
    Some(Contact("Beechdown Health Club", Some("Beechdown Park  Winchester Rd, Basingstoke, RG22 4ES"))))

  val call2 = ReceivedCall(
    at("2012-09-05T09:12T+01:00"),
    PhoneNumber("+441483550550", uk, guildford, "550550"),
    Some(Contact("University of Surrey", None)))

  val call3 = ReceivedCall(
    at("2012-11-05T15:10Z"),
    PhoneNumber("+441256362362", uk, basingstoke, "362362"),
    None)

  val call4 = ReceivedCall(
    at("2012-09-05T09:12T+01:00"),
    PhoneNumber("+1800362362", us, None, "800362362"),
    Some(Contact(
      "American Airlines",
      Some("Los Angeles International Airport, 400 World Way, Los Angeles, CA 90045"))))

  val call5 = ReceivedCall(
    at("2012-09-05T09:12T+01:00"),
    PhoneNumber("+447012550550", uk, None, "7012550550"),
    Some(Contact("University of Surrey", None)))

  val call6 = ReceivedCall(
    at("2012-11-05T15:10Z"),
    PhoneNumber("+33800162362", france, None, "800162362"),
    None)

  val uniqueCalls = List(call1, call2, call3, call4, call5, call6)
  val allCalls = (1 to 5).foldLeft(List[ReceivedCall]())((ls, _) => ls ::: uniqueCalls)

  def index = Action {
    val numberFormatter = new NumberFormatterImpl(new LocationConfiguration("44", "1256"))
    val allCallModels = allCalls.map(
      rc => {
        val pn = rc.phoneNumber
        CallModel(rc, numberFormatter.formatNumberAsInternational(pn), numberFormatter.formatAddress(pn))
      })
    Ok(views.html.index(allCallModels))
  }

  def at(formattedDate: String): Date = {
    null
  }

}