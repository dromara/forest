package org.dromara.forest.test.conf;

import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.backend.HttpBackendSelector;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.converter.ForestConverter;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.ssl.SSLUtils;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.RequestNameValue;
import junit.framework.Assert;
import org.dromara.forest.converter.json.ForestGsonConverter;
import org.dromara.forest.converter.json.ForestJacksonConverter;
import org.dromara.forest.converter.json.ForestJsonConverter;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.dromara.forest.mapping.MappingParameter.TARGET_BODY;
import static org.dromara.forest.mapping.MappingParameter.TARGET_HEADER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-08 12:40
 */
public class TestForestConfiguration {


    @Test
    public void testBackend() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        configuration.setBackendName("okhttp3");
        assertEquals("okhttp3", configuration.getBackend().getName());
        configuration.setBackend(null);
        configuration.setBackendName("httpclient");
        assertEquals("httpclient", configuration.getBackend().getName());

        HttpBackendSelector originSelector = new HttpBackendSelector();
        HttpBackendSelector selector = Mockito.spy(originSelector);
        configuration.setHttpBackendSelector(selector);
        Mockito.when(selector.findOkHttp3BackendInstance()).thenReturn(null);

        configuration.setBackendName(null);
        configuration.setBackend(null);
        Assert.assertEquals("httpclient", configuration.getBackend().getName());

        Mockito.when(selector.findHttpclientBackendInstance()).thenReturn(null);
        configuration.setBackendName(null);
        configuration.setBackend(null);

        boolean thrown = false;
        try {
            HttpBackend backend = configuration.getBackend();
            System.out.print(backend);
        } catch (ForestRuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testDefault() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        assertEquals("forestConfiguration" + configuration.hashCode(),
                configuration.getId());
        assertEquals(Integer.valueOf(3000), configuration.getTimeout());
        assertEquals(null, configuration.getConnectTimeout());
        assertEquals(null, configuration.getReadTimeout());
        assertEquals(Integer.valueOf(500), configuration.getMaxConnections());
        assertEquals(Integer.valueOf(500), configuration.getMaxRouteConnections());
        assertNotNull(configuration.getJsonConverter());
    }


    @Test
    public void testCustomized() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        configuration.setMaxRetryCount(3);
//        configuration.setId("config_2");
//        assertEquals("config_2", configuration.getId());
        assertEquals(Integer.valueOf(3), configuration.getMaxRetryCount());
        configuration.setMaxConnections(123);
        assertEquals(Integer.valueOf(123), configuration.getMaxConnections());
        configuration.setMaxRouteConnections(222);
        assertEquals(Integer.valueOf(222), configuration.getMaxRouteConnections());
        configuration.setTimeout(12000);
        assertEquals(Integer.valueOf(12000), configuration.getTimeout());
        configuration.setSslProtocol(SSLUtils.SSL_3);
        configuration.setConnectTimeout(2000);
    }

    @Test
    public void testVars() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        configuration.setVariableValue("name", "Peter");
        configuration.setVariableValue("baseUrl", "http://abc.com");
        assertEquals("Peter", configuration.getVariableValue("name"));
        assertEquals("http://abc.com", configuration.getVariableValue("baseUrl"));

        Map<String, Object> varMap = new HashMap<>();
        varMap.put("name", "Linda");
        varMap.put("abc", 123);
        configuration.setVariables(varMap);
        assertThat(configuration.getVariableValue("name")).isEqualTo("Linda");
        assertThat(configuration.getVariableValue("abc")).isEqualTo(123);
        AtomicInteger value = new AtomicInteger(0);
        configuration.setVariableValue("foo", (method) -> value.getAndIncrement());
        assertThat(configuration.getVariableValue("foo")).isEqualTo(0);
        assertThat(configuration.getVariableValue("foo")).isEqualTo(1);
    }


    @Test
    public void testDefaultParameters() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        List<RequestNameValue> defaultParameters = new LinkedList<>();
        defaultParameters.add(new RequestNameValue("name", "Peter", TARGET_BODY));
        defaultParameters.add(new RequestNameValue("age", "15", TARGET_BODY));
        configuration.setDefaultParameters(defaultParameters);
        assertEquals(defaultParameters, configuration.getDefaultParameters());
    }

    @Test
    public void testDefaultHeaders() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        List<RequestNameValue> defaultHeaders = new LinkedList<>();
        defaultHeaders.add(new RequestNameValue("Accept", "text/html", TARGET_HEADER));
        configuration.setDefaultHeaders(defaultHeaders);
        assertEquals(defaultHeaders, configuration.getDefaultHeaders());
    }

/**
 * TODO: 移动到 forest-fastjson 去
    @Test
    public void testConverterMap() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        assertNotNull(configuration.getConverterMap());
        Map<ForestDataType, ForestConverter> converterMap = new HashMap<>();
        converterMap.put(ForestDataType.JSON, new ForestFastjsonConverter());
        configuration.setConverterMap(converterMap);
        assertEquals(converterMap, configuration.getConverterMap());
    }
*/


}
