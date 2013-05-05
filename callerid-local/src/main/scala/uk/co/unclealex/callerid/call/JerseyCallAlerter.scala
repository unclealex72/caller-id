package uk.co.unclealex.callerid.call

import scala.collection.JavaConversions._
import org.apache.http.auth.AuthScope
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config.PROPERTY_CREDENTIALS_PROVIDER
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config.PROPERTY_PREEMPTIVE_BASIC_AUTHENTICATION
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config
import com.sun.jersey.client.apache4.ApacheHttpClient4
import com.sun.jersey.api.client.config.ClientConfig

/**
 * An implementation of {@link CallAlerter} that uses Jersey to communicate to a REST server.
 */
class JerseyCallAlerter(remoteConfiguration: RemoteConfiguration) extends CallAlerter {

  /**
   * The Jersey {@link Client} used to talk to the REST server.
   */
  val client = ApacheHttpClient4.create {
    val credentialsProvider = new CredentialsProvider() {
      def setCredentials(authscope: AuthScope, credentials: Credentials) = {}
      def getCredentials(authscope: AuthScope): Credentials = {
        new UsernamePasswordCredentials(remoteConfiguration.username, remoteConfiguration.password)
      }
      def clear: Unit = {}
    }
    val cc: ClientConfig = new DefaultApacheHttpClient4Config
    cc.getProperties() ++= Map(
      PROPERTY_PREEMPTIVE_BASIC_AUTHENTICATION -> Boolean.box(true),
      PROPERTY_CREDENTIALS_PROVIDER -> credentialsProvider)
    cc
  }

  val url: String = remoteConfiguration.url

  override def callMade(number: String) = {
    client.resource(url).post(classOf[String], number)
  }

}