package uk.co.unclealex.callerid.call

import com.sun.jersey.api.client.Client
import com.sun.jersey.client.apache.ApacheHttpClient
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig
import javax.inject.Inject
import org.eclipse.xtend.lib.Property

import static com.sun.jersey.client.apache.config.ApacheHttpClientConfig.*

/**
 * An implementation of {@link CallAlerter} that uses Jersey to communicate to a REST server.
 */
class JerseyCallAlerter implements CallAlerter {

    /**
     * The Jersey {@link Client} used to talk to the REST server.
     */
    @Property val Client client;
    
    /**
     * The URL of the REST server.
     */
    @Property val String url;
    
    @Inject
    public new(RemoteConfiguration remoteConfiguration) {
      _url = remoteConfiguration.url;
      val ApacheHttpClientConfig cc = new DefaultApacheHttpClientConfig();
      cc.properties.put(PROPERTY_PREEMPTIVE_AUTHENTICATION, true);
      cc.state.setCredentials(null, null, -1, remoteConfiguration.username, remoteConfiguration.password);
      _client = ApacheHttpClient::create(cc);
    }
    
    override String callMade(String number) {
      client.resource(url).post(typeof(String), number);
    }
    
}