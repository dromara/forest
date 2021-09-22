package com.dtflys.test.conf;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.HttpBackendSelector;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.ssl.SSLUtils;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.RequestNameValue;
import junit.framework.Assert;
import com.dtflys.forest.converter.json.JSONConverterSelector;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.converter.json.ForestGsonConverter;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.dtflys.forest.mapping.MappingParameter.TARGET_BODY;
import static com.dtflys.forest.mapping.MappingParameter.TARGET_HEADER;
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
        assertEquals(Integer.valueOf(2000), configuration.getConnectTimeout());
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
        configuration.setVariableValue("foo", (args) -> "bar");
        assertThat(configuration.getVariableValue("foo")).isEqualTo("bar");
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

    @Test
    public void testConverterMap() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        assertNotNull(configuration.getConverterMap());
        Map<ForestDataType, ForestConverter> converterMap = new HashMap<>();
        converterMap.put(ForestDataType.JSON, new ForestFastjsonConverter());
        configuration.setConverterMap(converterMap);
        assertEquals(converterMap, configuration.getConverterMap());
    }

    @Test
    public void testJSONConvertSelectCheck() throws Throwable {
        JSONConverterSelector jsonConverterSelector = new JSONConverterSelector();
        try {
            Class fastJsonClass = jsonConverterSelector.checkFastJSONClass();
            assertNotNull(fastJsonClass);
        } catch (Throwable th) {
        }
        try {
            Class gsonClass = jsonConverterSelector.checkGsonClass();
            assertNotNull(gsonClass);
        } catch (Throwable th) {
        }
        try {
            Class jacson = jsonConverterSelector.checkJacsonClass();
            assertNotNull(jacson);
        } catch (Throwable th) {
        }
    }

    @Test
    public void testJSONConverterSelect() throws Throwable {
        JSONConverterSelector jsonConverterSelector = new JSONConverterSelector();
        JSONConverterSelector spy = Mockito.spy(jsonConverterSelector);
        Mockito.when(spy.checkFastJSONClass())
                .thenThrow(new ClassNotFoundException("com.alibaba.fastjson.JSON"));

        ForestJsonConverter jsonConverter = spy.select();
        assertNotNull(jsonConverter);
        assertTrue(jsonConverter instanceof ForestJacksonConverter);

        jsonConverterSelector = new JSONConverterSelector();
        spy = Mockito.spy(jsonConverterSelector);
        Mockito.when(spy.checkFastJSONClass())
                .thenThrow(new ClassNotFoundException("com.alibaba.fastjson.JSON"));
        Mockito.when(spy.checkJacsonClass())
                .thenThrow(new ClassNotFoundException("com.fasterxml.jackson.databind.ObjectMapper"));
        jsonConverter = spy.select();
        assertNotNull(jsonConverter);
        assertTrue(jsonConverter instanceof ForestGsonConverter);

        jsonConverterSelector = new JSONConverterSelector();
        spy = Mockito.spy(jsonConverterSelector);
        Mockito.when(spy.checkFastJSONClass())
                .thenThrow(new ClassNotFoundException("com.alibaba.fastjson.JSON"));
        Mockito.when(spy.checkJacsonClass())
                .thenThrow(new ClassNotFoundException("com.fasterxml.jackson.databind.ObjectMapper"));
        Mockito.when(spy.checkGsonClass())
                .thenThrow(new ClassNotFoundException("com.google.gson.JsonParser"));
        jsonConverter = spy.select();
        assertNull(jsonConverter);
    }

}
