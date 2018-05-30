package squeezebox

import scala.concurrent.Future

trait Squeezebox {

  def display(text: String): Future[Seq[String]]
}
