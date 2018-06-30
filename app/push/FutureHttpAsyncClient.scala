package push

import java.io.IOException
import java.util.function.Consumer

import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.concurrent.FutureCallback
import org.apache.http.nio.client.HttpAsyncClient
import org.apache.http.nio.protocol.{HttpAsyncRequestProducer, HttpAsyncResponseConsumer}
import org.apache.http.protocol.HttpContext
import org.apache.http.{HttpHost, HttpRequest, HttpResponse}

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * An adaptor for [[HttpAsyncClient]] that allows results to be returned as Scala futures.
  */
class FutureHttpAsyncClient(val httpAsyncClient: HttpAsyncClient)(implicit ec: ExecutionContext) {
  def execute[T](
                  httpAsyncRequestProducer: HttpAsyncRequestProducer,
                  httpAsyncResponseConsumer: HttpAsyncResponseConsumer[T],
                  httpContext: HttpContext): Future[T] =
    toFuture { fc: FutureCallback[T] =>
      httpAsyncClient.execute(httpAsyncRequestProducer, httpAsyncResponseConsumer, httpContext, fc) }

  def execute[T](
                  httpAsyncRequestProducer: HttpAsyncRequestProducer,
                  httpAsyncResponseConsumer: HttpAsyncResponseConsumer[T]): Future[T] =
    toFuture { fc: FutureCallback[T] =>
      httpAsyncClient.execute(httpAsyncRequestProducer, httpAsyncResponseConsumer, fc) }

  def execute(
               httpHost: HttpHost,
               httpRequest: HttpRequest,
               httpContext: HttpContext): Future[HttpResponse] =
    toFuture[HttpResponse] { fc: FutureCallback[HttpResponse] =>
      httpAsyncClient.execute(httpHost, httpRequest, httpContext, fc) }

  def execute(
               httpHost: HttpHost,
               httpRequest: HttpRequest): Future[HttpResponse] =
    toFuture { fc: FutureCallback[HttpResponse] =>
      httpAsyncClient.execute(httpHost, httpRequest, fc)
  }

  def execute(
               httpUriRequest: HttpUriRequest,
               httpContext: HttpContext): Future[HttpResponse] =
    toFuture { fc: FutureCallback[HttpResponse] =>
      httpAsyncClient.execute(httpUriRequest, httpContext, fc) }

  def execute(httpUriRequest: HttpUriRequest): Future[HttpResponse] =
    toFuture { fc: FutureCallback[HttpResponse] =>
      httpAsyncClient.execute(httpUriRequest, fc)
    }

  /**
    * Convert a [[FutureCallback]] into a [[Future]]
    * @param c The HTTP response Consumer.
    * @tparam T The type of object to return.
    * @return
    */
  private def toFuture[T](c: Consumer[FutureCallback[T]]): Future[T] = {
    val promise: Promise[T] = Promise[T]
    c.accept(new FutureCallback[T]() {
      override def completed(t: T): Unit = {
        promise.success(t)
      }
      override def failed(e: Exception): Unit = {
        promise.failure(e)
      }

      override def cancelled(): Unit = {
        promise.failure(new IOException("Cancelled"))
      }
    })
    promise.future
  }
}
