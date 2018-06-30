package push

/**
  * Configuration for browser notifications.
  * @param publicKey The VAPID public key.
  * @param privateKey The VAPID private key.
  * @param domain The web domain.
  */
case class BrowserPushConfiguration(publicKey: String, privateKey: String, domain: String)