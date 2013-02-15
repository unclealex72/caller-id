package uk.co.unclealex.callerid.call

import org.junit.Test
import static org.junit.Assert.*;
import org.codehaus.jackson.map.ObjectMapper

class RemoteConfigurationBeanTest {
    
    @Test
    def testDeserialisation() {
        var json = '{
            "username" : "Brian",
            "password" : "Br1an",
            "url" : "https://www.somewhere.com/api"
        }'
        val reader = new ObjectMapper().reader(typeof(RemoteConfigurationBean));
        val RemoteConfigurationBean actualBean = reader.readValue(json);
        val expectedBean = new RemoteConfigurationBean("https://www.somewhere.com/api", "Brian", "Br1an");
        assertEquals("The bean was deserialised incorrectly.", expectedBean, actualBean);
    }    
}