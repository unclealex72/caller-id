package auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

/**
  * The Silhouette environment to use. [[User]] is used to represent identities and [[CookieAuthenticator]] is
  * used to authenticate.
  */
trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}