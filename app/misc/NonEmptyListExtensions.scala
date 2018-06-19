package misc

import cats.data.NonEmptyList

object NonEmptyListExtensions {

  implicit class NonEmptyListImplicits[A](nonEmptyList: NonEmptyList[A]) {
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
