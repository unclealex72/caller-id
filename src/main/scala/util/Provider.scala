package util

/**
 * A trait that provides another type, allowing for prototype injection.
 */
trait Provider[A] {

  def get: A

  def withProvided[B](block: A => B): B = {
    val provided = get
    try {
      block(provided)
    }
    finally {
      provided match {
        case closeable: AutoCloseable => closeable.close()
        case _ =>
      }
    }
  }
}

object Provider {
  def singleton[A](a: A): Provider[A] = new Provider[A] {
    override def get: A = a
  }
}