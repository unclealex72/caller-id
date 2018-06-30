package datetime

import java.lang.{Long => JLong, String => JString}
import java.util.{Map => JMap}

/**
  * A trait used to allow dates to be formatted with the suffixes <i>st</i>, <i>nd</i>, <i>rd</i> and <i>th</i>
  * to the day of the month.
  */
trait DaySuffixes {

  /**
    *
    * @return A map of days and their suffixes.
    */
  def suffixes: JMap[JLong, JString]
}
