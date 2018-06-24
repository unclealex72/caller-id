package datetime

import java.lang.{Long => JLong, String => JString}
import java.util.{Map => JMap}

trait DaySuffixes {

  def suffixes: JMap[JLong, JString]
}
