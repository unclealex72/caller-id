package datetime

import java.lang.{Long => JLong, String => JString}
import java.util.{Map => JMap}

import scala.collection.JavaConverters._

object DaySuffixesImpl extends DaySuffixes {

  override val suffixes: JMap[JLong, JString] = Range(1, 31).map { day =>
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

}
