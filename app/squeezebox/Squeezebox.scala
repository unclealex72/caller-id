package squeezebox

import scala.concurrent.{ExecutionContext, Future}

trait Squeezebox {

  def display(text: String)(implicit ec: ExecutionContext): Future[_]
}
