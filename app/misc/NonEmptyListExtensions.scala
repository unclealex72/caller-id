package misc

import cats.data.NonEmptyList

/**
  * Extensions to [[NonEmptyList]]
  */
object NonEmptyListExtensions {

  implicit class NonEmptyListImplicits[A](nonEmptyList: NonEmptyList[A]) {

    /**
      * Join strings together so that they are of the form 1, 2 or 3.
      * @param joinString The string used to join all but the last two elements.
      * @param lastJoinString The string used to join the last two elements.
      * @return A string formatted like 1, 2, 3 or 4.
      */
    def join(joinString: String, lastJoinString: String): String = {
      val (init: List[String], last: String) =
        nonEmptyList.tail.foldLeft((List.empty[String], nonEmptyList.head.toString)) { (acc, message) =>
          val (init, last) = acc
          (init :+ last, message.toString)
        }
      init match {
        case Nil => last
        case list => Seq(list.mkString(joinString), last).mkString(lastJoinString)
      }
    }
  }
}
