package number

/**
 * A formattable version of a phone number.
 * Created by alex on 13/09/15.
 */
case class FormattableNumber(internationalCode: Option[String], stdCode: Option[String], number: String) {

  def std(newStdCode: String) = FormattableNumber(internationalCode, Some(newStdCode), number)
  def int(newInternationalCode: String) = FormattableNumber(Some(newInternationalCode), stdCode, number)

  def default: String = {
    Seq(internationalCode.map(c => s"+$c"), stdCode.map(s => s"($s)"), Some(number)).flatten.mkString(" ")
  }
}

object FormattableNumber {
  def apply(number: String): FormattableNumber = FormattableNumber(None, None, number)
}