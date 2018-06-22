package datetime

import java.util.{Map => JMap}
import java.lang.{Long => JLong, String => JString}

trait DaySuffixes {

  def suffixes: JMap[JLong, JString]
}
