package org.apache.axis2.spring.boot.client;

import org.apache.axis2.client.Options;
import org.apache.axis2.kernel.TransportListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class Axis2ClientAutoConfigurationTest {

    @Test
    void mapsConfiguredOptionsAndSkipsAbsentListener() throws Exception {
        Axis2ClientProperties properties = new Axis2ClientProperties();
        properties.getOptions().setAction("urn:send");
        properties.getOptions().setUserName("alice");
        properties.getOptions().setPassword("secret");
        properties.getOptions().setTimeOutInMilliSeconds(1500);
        properties.getOptions().setProperties(Map.of("region", "test"));
        ObjectProvider<TransportListener> listeners =
                new DefaultListableBeanFactory().getBeanProvider(TransportListener.class);

        Options result = new Axis2ClientAutoConfiguration().overrideOptions(properties, listeners);

        assertEquals("urn:send", result.getAction());
        assertEquals("alice", result.getUserName());
        assertEquals("secret", result.getPassword());
        assertEquals(1500, result.getTimeOutInMilliSeconds());
        assertEquals("test", result.getProperty("region"));
        assertNull(result.getListener());
    }
}
