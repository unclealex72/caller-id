package dialogflow

import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder, TextStyle}
import java.time.temporal.ChronoField

import scala.collection.JavaConverters._
import java.util.{Map => JMap}
import java.lang.{Long => JLong, String => JString}
object WebhookResponseDateTimeFormatter {

  private val days: JMap[JLong, JString] = Range(1, 31).map { day =>
    val maybeSuffix: Option[String] = for {
      dy <- Some(day) if (day / 10) != 1
      suffix <- dy % 10 match {
        case 1 => Some("st")
        case 2 => Some("nd")
        case 3 => Some("rd")
        case _ => None
      }
    } yield {
      suffix
    }
    JLong.valueOf(day) -> s"$day${maybeSuffix.getOrElse("th")}"
  }.toMap.asJava

  private val am_pm: JMap[JLong, JString] = Map(JLong.valueOf(0) -> "a m", JLong.valueOf(1) -> "p m").asJava

  private val dateTimeFormatter: DateTimeFormatter =
    new DateTimeFormatterBuilder().
      appendText(ChronoField.DAY_OF_WEEK, TextStyle.FULL).
      appendLiteral(" the ").
      appendText(ChronoField.DAY_OF_MONTH, days).
      appendLiteral(" of ").
      appendText(ChronoField.MONTH_OF_YEAR, TextStyle.FULL).
      appendLiteral(" at ").
      appendValue(ChronoField.CLOCK_HOUR_OF_AMPM).
      appendLiteral(' ').
      appendValue(ChronoField.MINUTE_OF_HOUR, 2).
      appendLiteral(' ').
      appendText(ChronoField.AMPM_OF_DAY, am_pm).
      toFormatter

  def apply(): DateTimeFormatter = dateTimeFormatter
}
