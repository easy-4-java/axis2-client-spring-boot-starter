package org.apache.axis2.spring.boot.client;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;

import javax.xml.namespace.QName;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class Axis2ClientTemplateTest {
    @Test
    void documentClientBuildsSoapPayloadAndSupportsAsyncCalls() throws Exception {
        Options override = new Options();
        ServiceClient client = mock(ServiceClient.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        Options options = new Options();
        when(client.getOptions()).thenReturn(options);
        OMElement response = OMAbstractFactory.getOMFactory().createOMElement("response", null);
        when(client.sendReceive(any(OMElement.class))).thenReturn(response);
        Axis2DocClientTemplate template = new Axis2DocClientTemplate(override) {
            @Override protected ServiceClient createServiceClient() { return client; }
        };

        assertSame(response, template.sendReceive("http://localhost/service", "urn:test:", "send", Map.of("sku", "42")));
        assertEquals("urn:test:send", options.getAction());
        org.mockito.ArgumentCaptor<org.apache.axis2.addressing.EndpointReference> endpoint =
                org.mockito.ArgumentCaptor.forClass(org.apache.axis2.addressing.EndpointReference.class);
        verify(client).setTargetEPR(endpoint.capture());
        assertEquals("http://localhost/service", endpoint.getValue().getAddress());
        verify(client).setOverrideOptions(override);
        org.mockito.ArgumentCaptor<OMElement> request = org.mockito.ArgumentCaptor.forClass(OMElement.class);
        verify(client).sendReceive(request.capture());
        assertEquals("send", request.getValue().getLocalName());
        assertEquals("42", request.getValue().getFirstElement().getText());

        assertSame(response, template.sendReceive("http://localhost/service", "urn:test:", "send"));
        assertSame(response, template.sendReceive("http://localhost/service", "explicit", "urn:test:", "send"));
        assertEquals("explicit", options.getAction());
        template.sendReceiveNonBlocking("http://localhost/service", "async", "urn:test:", "send",
                Map.of("sku", "43"), null);
        verify(client).sendReceiveNonBlocking(any(OMElement.class), isNull());
        assertEquals("async", options.getAction());
    }

    @Test
    void rpcClientSetsActionAndReturnsBothResponseForms() throws Exception {
        Options override = new Options();
        RPCServiceClient client = mock(RPCServiceClient.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        Options options = new Options();
        when(client.getOptions()).thenReturn(options);
        OMElement response = OMAbstractFactory.getOMFactory().createOMElement("response", null);
        when(client.invokeBlocking(any(QName.class), any(Object[].class))).thenReturn(response);
        Object[] typed = new Object[] { "ok" };
        when(client.invokeBlocking(any(QName.class), any(Object[].class), any(Class[].class))).thenReturn(typed);
        Axis2RpcClientTemplate template = new Axis2RpcClientTemplate(override) {
            @Override protected RPCServiceClient createRPCServiceClient() { return client; }
        };
        Object[] args = { "sku" };
        Class<?>[] types = { String.class };

        assertSame(response, template.invoke("http://localhost/wsdl", "urn:test:", "send", args));
        assertEquals("urn:test:send", options.getAction());
        assertEquals("http://localhost/wsdl", options.getTo().getAddress());
        assertSame(response, template.invoke("http://localhost/wsdl", "explicit", "urn:test:", "send", args));
        assertEquals("explicit", options.getAction());
        assertSame(typed, template.invoke("http://localhost/wsdl", "urn:test:", "send", args, types));
        assertSame(typed, template.invoke("http://localhost/wsdl", "explicit", "urn:test:", "send", args, types));
        verify(client, times(4)).setOverrideOptions(override);
        verify(client, times(2)).invokeBlocking(eq(new QName("urn:test:", "send")), same(args));
        verify(client, times(2)).invokeBlocking(eq(new QName("urn:test:", "send")), same(args), same(types));
    }
}
