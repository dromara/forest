package com.dtflys.test.proxy;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.forest.proxy.InterfaceProxyHandler;
import com.dtflys.forest.proxy.ProxyFactory;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 19:32
 */
public class TestInterfaceProxyHandler {

    private static ForestConfiguration configuration = ForestConfiguration.configuration();


    @Test
    public void testGetProxyFactory() {
        ProxyFactory<GetClient> getClientProxyFactory = new ProxyFactory<>(configuration, GetClient.class);
        InterfaceProxyHandler<GetClient> interfaceProxyHandler =
                new InterfaceProxyHandler(configuration, getClientProxyFactory, GetClient.class);
        assertEquals(getClientProxyFactory, interfaceProxyHandler.getProxyFactory());
    }

    @BaseRequest(baseURL = "http://localhost")
    interface LocalhostBaseURLClient {
    }


    @BaseRequest(baseURL = "localhost")
    interface NonProtocolBaseURLClient {
    }


    @BaseRequest(baseURL = "")
    interface EmptyBaseURLClient {
    }

    interface RenamedMethodClient {

        @Request(url = "http://localhost/misc")
        String test();

        @Request(url = "http://localhost/misc/${0}")
        String test(String a);

    }


    @Test
    public void testBaseURL() {
        ProxyFactory<LocalhostBaseURLClient> getClientProxyFactory = new ProxyFactory<>(configuration, LocalhostBaseURLClient.class);
        InterfaceProxyHandler<LocalhostBaseURLClient> interfaceProxyHandler =
                new InterfaceProxyHandler(configuration, getClientProxyFactory, LocalhostBaseURLClient.class);
        MetaRequest metaRequest = interfaceProxyHandler.getBaseMetaRequest();
        assertThat(metaRequest.getUrl()).isEqualTo("http://localhost");
    }

    @Test
    public void testNonProtocolBaseURL() {
        ProxyFactory<NonProtocolBaseURLClient> getClientProxyFactory = new ProxyFactory<>(configuration, NonProtocolBaseURLClient.class);
        InterfaceProxyHandler<NonProtocolBaseURLClient> interfaceProxyHandler =
                new InterfaceProxyHandler(configuration, getClientProxyFactory, NonProtocolBaseURLClient.class);
        MetaRequest metaRequest = interfaceProxyHandler.getBaseMetaRequest();
        assertThat(metaRequest.getUrl()).isEqualTo("localhost");
    }



    @Test
    public void testEmptyBaseURL() {
        ProxyFactory<EmptyBaseURLClient> getClientProxyFactory = new ProxyFactory<>(configuration, EmptyBaseURLClient.class);
        InterfaceProxyHandler<EmptyBaseURLClient> interfaceProxyHandler =
                new InterfaceProxyHandler(configuration, getClientProxyFactory, EmptyBaseURLClient.class);
        MetaRequest metaRequest = interfaceProxyHandler.getBaseMetaRequest();
        assertNull(metaRequest.getUrl());
    }


    @Test
    public void testRenamedMethod() {
        ProxyFactory<RenamedMethodClient> getClientProxyFactory = new ProxyFactory<>(configuration, RenamedMethodClient.class);
        InterfaceProxyHandler<RenamedMethodClient> interfaceProxyHandler =
                new InterfaceProxyHandler(configuration, getClientProxyFactory, RenamedMethodClient.class);
    }


}
