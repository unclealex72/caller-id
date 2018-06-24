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


object FutureHttpAsyncClient {
  private def toFuture[T](c: Consumer[FutureCallback[T]])(implicit ec: ExecutionContext): Future[T] = {
    val promise = Promise[T]
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

class FutureHttpAsyncClient(val httpAsyncClient: HttpAsyncClient) {
  def execute[T](
                  httpAsyncRequestProducer: HttpAsyncRequestProducer,
                  httpAsyncResponseConsumer: HttpAsyncResponseConsumer[T],
                  httpContext: HttpContext)(implicit ec: ExecutionContext): Future[T] =
    FutureHttpAsyncClient.toFuture { fc: FutureCallback[T] =>
      httpAsyncClient.execute(httpAsyncRequestProducer, httpAsyncResponseConsumer, httpContext, fc) }

  def execute[T](
                  httpAsyncRequestProducer: HttpAsyncRequestProducer,
                  httpAsyncResponseConsumer: HttpAsyncResponseConsumer[T])(implicit ec: ExecutionContext): Future[T] =
    FutureHttpAsyncClient.toFuture { fc: FutureCallback[T] =>
      httpAsyncClient.execute(httpAsyncRequestProducer, httpAsyncResponseConsumer, fc) }

  def execute(
               httpHost: HttpHost,
               httpRequest: HttpRequest,
               httpContext: HttpContext)(implicit ec: ExecutionContext): Future[HttpResponse] =
    FutureHttpAsyncClient.toFuture[HttpResponse] { fc: FutureCallback[HttpResponse] =>
      httpAsyncClient.execute(httpHost, httpRequest, httpContext, fc) }

  def execute(
               httpHost: HttpHost,
               httpRequest: HttpRequest)(implicit ec: ExecutionContext): Future[HttpResponse] =
    FutureHttpAsyncClient.toFuture { fc: FutureCallback[HttpResponse] =>
      httpAsyncClient.execute(httpHost, httpRequest, fc)
  }

  def execute(
               httpUriRequest: HttpUriRequest,
               httpContext: HttpContext)(implicit ec: ExecutionContext): Future[HttpResponse] =
    FutureHttpAsyncClient.toFuture { fc: FutureCallback[HttpResponse] =>
      httpAsyncClient.execute(httpUriRequest, httpContext, fc) }

  def execute(httpUriRequest: HttpUriRequest)(implicit ec: ExecutionContext): Future[HttpResponse] =
    FutureHttpAsyncClient.toFuture { fc: FutureCallback[HttpResponse] =>
      httpAsyncClient.execute(httpUriRequest, fc)
    }
}
