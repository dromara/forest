package com.dtflys.test.proxy;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.forest.proxy.ProxyFactory;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 19:28
 */
public class TestProxyFactory {

    private static ForestConfiguration configuration = ForestConfiguration.configuration();

    private static ProxyFactory<GetClient> getClientProxyFactory = new ProxyFactory<>(configuration, GetClient.class);


    @Test
    public void testProxyFactory() {
        assertEquals(GetClient.class, getClientProxyFactory.getInterfaceClass());
        getClientProxyFactory.setInterfaceClass(GetClient.class);
        assertEquals(GetClient.class, getClientProxyFactory.getInterfaceClass());

        Object getClient = getClientProxyFactory.createInstance();
        assertNotNull(getClient);
        assertTrue(getClient instanceof GetClient);
    }


}
