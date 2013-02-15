package uk.co.unclealex.callerid.call

import org.codehaus.jackson.annotate.JsonProperty
import org.eclipse.xtend.lib.Data

/**
 * A JSON aware bean implementation of {@link RemoteConfiguration}. 
 */
@Data
class RemoteConfigurationBean implements RemoteConfiguration {
    
  /**
   * The URL where the remote REST service resides.
   */
  var String url;
  
  /**
   * The username used when authenticating.
   */
  var String username;
  
  /**
   * The password used when authenticating.
   */
  var String password;
  
  new(@JsonProperty("url") String url, @JsonProperty("username") String username, @JsonProperty("password") String password) {
      _url = url;
      _username = username;
      _password = password;
  }
}