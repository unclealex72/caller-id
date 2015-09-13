package time

import org.joda.time.DateTime

/**
 * Created by alex on 04/09/15.
 */
trait NowService {

  def now: DateTime
}

object NowService {
  def apply(): NowService = new NowService {
    def now = DateTime.now()
  }

  def at(dateTime: DateTime) = new NowService {
    def now = dateTime
  }
}