package uk.co.unclealex.callerid.call

import com.sun.jersey.api.client.Client
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig
import com.sun.jersey.client.apache.ApacheHttpClient

/**
 * An implementation of {@link CallAlerter} that uses Jersey to communicate to a REST server.
 */
class JerseyCallAlerter(remoteConfiguration: RemoteConfiguration) extends CallAlerter {

  /**
   * The Jersey {@link Client} used to talk to the REST server.
   */
  val client: Client = ApacheHttpClient.create({
    val cc = new DefaultApacheHttpClientConfig
    cc.getProperties().put(com.sun.jersey.client.apache.config.ApacheHttpClientConfig.PROPERTY_PREEMPTIVE_AUTHENTICATION, true: java.lang.Boolean)
    cc.getState().setCredentials(null, null, -1, remoteConfiguration.username, remoteConfiguration.password);
    cc
  })

  val url: String = remoteConfiguration.url

  override def callMade(number: String) = {
    client.resource(url).post(classOf[String], number)
  }

}