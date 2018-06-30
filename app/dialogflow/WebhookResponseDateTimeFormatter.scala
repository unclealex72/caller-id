package dialogflow

import java.lang.{Long => JLong, String => JString}
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder, TextStyle}
import java.time.temporal.ChronoField
import java.util.{Map => JMap}

import datetime.DaySuffixes

import scala.collection.JavaConverters._

/**
  * A [[DateTimeFormatter]] that produces days and times as they are spoken. For example:
  * "Thursday the 5th of June at 9 32 a m". Note the extra spaces so that am and pm are correctly pronounced.
  * @param daySuffixes The suffix for each day of the month.
  */
class WebhookResponseDateTimeFormatter(daySuffixes: DaySuffixes) {

  private val am_pm: JMap[JLong, JString] = Map(JLong.valueOf(0) -> "a m", JLong.valueOf(1) -> "p m").asJava

  private val dateTimeFormatter: DateTimeFormatter =
    new DateTimeFormatterBuilder().
      appendText(ChronoField.DAY_OF_WEEK, TextStyle.FULL).
      appendLiteral(" the ").
      appendText(ChronoField.DAY_OF_MONTH, daySuffixes.suffixes).
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
