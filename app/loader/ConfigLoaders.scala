package loader

import java.time.ZoneId

import com.mohiva.play.silhouette.crypto.{JcaCrypterSettings, JcaSignerSettings}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticatorSettings, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.OAuth2Settings
import com.typesafe.config.Config
import play.api.{ConfigLoader, Configuration}
import push.BrowserPushConfiguration

import scala.collection.JavaConverters._
import scala.concurrent.duration.FiniteDuration

/**
  * A list of [[ConfigLoader]]s used to parse the application configuration file.
  */
object ConfigLoaders {

  private abstract class ConfigurationLoader[T] extends ConfigLoader[T] {
    override def load(config: Config, path: String): T = {
      load(path, Configuration(config))
    }

    def load(implicit path: String, configuration: Configuration): T
  }

  private implicit class MaybeDefaultImplicits[T](t: T) {
    def maybe[R](parameterName: String, mutator: R => T => T)(implicit path: String, configuration: Configuration, configLoader: ConfigLoader[R]): T = {
      configuration.getOptional[R](s"$path.$parameterName") match {
        case Some(parameter) => mutator(parameter)(t)
        case None => t
      }
    }
  }

  private implicit class GetImplicits(key: String) {
    def get[R](implicit configuration: Configuration, configLoader: ConfigLoader[R], rootPath: String): R = {
      configuration.get[R](s"$rootPath.$key")
    }
  }

  implicit val jcaSignerSettingsConfigLoader: ConfigLoader[JcaSignerSettings] = new ConfigurationLoader[JcaSignerSettings] {
    override def load(implicit path: String, configuration: Configuration): JcaSignerSettings = {
      val key: String = "key".get[String]
      JcaSignerSettings(key).maybe[String]("pepper", pepper => _.copy(pepper = pepper))
    }
  }

  implicit val oauth2settingsConfigLoader: ConfigLoader[OAuth2Settings] = new ConfigurationLoader[OAuth2Settings] {
    override def load(implicit path: String, configuration: Configuration): OAuth2Settings = {
      val accessTokenURL: String = "accessTokenURL".get[String]
      val clientID: String = "clientID".get[String]
      val clientSecret: String = "clientSecret".get[String]
      OAuth2Settings(
        accessTokenURL = accessTokenURL,
        clientID = clientID,
        clientSecret = clientSecret).
        maybe[String]("authorizationURL", url => _.copy(authorizationURL = Some(url))).
        maybe[String]("redirectURL", url => _.copy(redirectURL = Some(url))).
        maybe[String]("apiURL", url => _.copy(apiURL = Some(url))).
        maybe[String]("scope", scope => _.copy(scope = Some(scope))).
        maybe[Map[String, String]]("authorizationParams", params => _.copy(authorizationParams = params)).
        maybe[Map[String, String]]("accessTokenParams", params => _.copy(accessTokenParams = params)).
        maybe[Map[String, String]]("customProperties", customProperties => _.copy(customProperties = customProperties))
    }
  }

  implicit val jcaCrypterSettingsConfigLoader: ConfigLoader[JcaCrypterSettings] = new ConfigurationLoader[JcaCrypterSettings] {
    override def load(implicit path: String, configuration: Configuration): JcaCrypterSettings = {
      val key: String = "key".get[String]
      JcaCrypterSettings(key)
    }
  }

  implicit val jwtAuthenticatorServiceConfigLoader: ConfigLoader[JWTAuthenticatorSettings] = new ConfigurationLoader[JWTAuthenticatorSettings] {
    override def load(implicit path: String, configuration: Configuration): JWTAuthenticatorSettings = {
      val sharedSecret: String = "sharedSecret".get[String]
      JWTAuthenticatorSettings(sharedSecret = sharedSecret).
        maybe[String]("fieldName", fieldName => _.copy(fieldName = fieldName)).
        maybe[String]("issuerClaim", issuerClaim => _.copy(issuerClaim = issuerClaim)).
        maybe[FiniteDuration]("authenticatorIdleTimeout", authenticatorIdleTimeout => _.copy(authenticatorIdleTimeout = Some(authenticatorIdleTimeout))).
        maybe[FiniteDuration]("authenticatorExpiry", authenticatorExpiry => _.copy(authenticatorExpiry = authenticatorExpiry))
    }
  }

  implicit val modemConfigurationConfigLoader: ConfigLoader[ModemConfiguration] = new ConfigurationLoader[ModemConfiguration] {
    override def load(implicit path: String, configuration: Configuration): ModemConfiguration = {
      val useDebug: Boolean = "debug".get[Boolean]
      if (useDebug) {
        DebugModemConfiguration
      }
      else {
        val host: String = "host".get[String]
        val port: Int = "port".get[Int]
        NetworkModemConfiguration(host, port)
      }
    }
  }

  implicit def zoneIdConfigLoader(implicit stringConfigLoader: ConfigLoader[String]): ConfigLoader[ZoneId] =
    (config: Config, path: String) => {
      val validZoneIds: Set[String] = {
        val empty: Set[String] = Set.empty
        ZoneId.getAvailableZoneIds.asScala.foldLeft(empty)(_ + _)
      }
      val zoneId: String = Configuration(config).getAndValidate[String](path, validZoneIds)
      ZoneId.of(zoneId)
  }

  implicit val networkConfigurationConfigLoader: ConfigLoader[NetworkSqueezeboxConfiguration] = new ConfigurationLoader[NetworkSqueezeboxConfiguration] {
    override def load(implicit path: String, configuration: Configuration): NetworkSqueezeboxConfiguration = {
      val host: String = "host".get[String]
      val port: Int = "port".get[Int]
      val duration: FiniteDuration = "duration".get[FiniteDuration]
      NetworkSqueezeboxConfiguration(host = host, port = port, duration = duration)
    }
  }
  implicit val browserPushConfigurationConfigLoader: ConfigLoader[BrowserPushConfiguration] = new ConfigurationLoader[BrowserPushConfiguration] {
    override def load(implicit path: String, configuration: Configuration): BrowserPushConfiguration = {
      val privateKey: String = "keys.private".get[String]
      val publicKey: String = "keys.public".get[String]
      val domain: String = "domain".get[String]
      BrowserPushConfiguration(publicKey = publicKey, privateKey = privateKey, domain = domain)
    }
  }

  implicit val cookieAuthenticatorSettingsConfigLoader: ConfigLoader[CookieAuthenticatorSettings] = new ConfigurationLoader[CookieAuthenticatorSettings] {
    override def load(implicit path: String, configuration: Configuration): CookieAuthenticatorSettings = {
      CookieAuthenticatorSettings().
        maybe[String]("name", name => _.copy(cookieName = name)).
        maybe[String]("path", path => _.copy(cookiePath = path)).
        maybe[String]("domain", domain => _.copy(cookieDomain = Some(domain))).
        maybe[Boolean]("secure", secure => _.copy(secureCookie = secure)).
        maybe[Boolean]("httpOnly", httpOnly => _.copy(httpOnlyCookie = httpOnly)).
        maybe[Boolean]("useFingerprinting", fingerprinting => _.copy(useFingerprinting = fingerprinting)).
        maybe[FiniteDuration]("maxAge", maxAge => _.copy(cookieMaxAge = Some(maxAge))).
        maybe[FiniteDuration]("idleTimeout", timeout => _.copy(authenticatorIdleTimeout = Some(timeout))).
        maybe[FiniteDuration]("expiry", expiry => _.copy(authenticatorExpiry = expiry))
    }
  }
}
