package org.forest.test.conf;

import org.forest.config.ForestConfiguration;
import org.forest.converter.ForestConverter;
import org.forest.converter.json.JSONConverterSelector;
import org.forest.converter.json.ForestFastjsonConverter;
import org.forest.converter.json.ForestGsonConverter;
import org.forest.converter.json.ForestJacksonConverter;
import org.forest.converter.json.ForestJsonConverter;
import org.forest.utils.ForestDataType;
import org.forest.utils.RequestNameValue;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-08 12:40
 */
public class TestForestConfiguration {

    @Test
    public void testDefault() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        assertEquals("forestConfiguration" + configuration.hashCode(),
                configuration.getId());
        assertEquals(Integer.valueOf(3000), configuration.getTimeout());
        assertEquals(Integer.valueOf(2000), configuration.getConnectTimeout());
        assertEquals(Integer.valueOf(500), configuration.getMaxConnections());
        assertEquals(Integer.valueOf(500), configuration.getMaxRouteConnections());
        assertNotNull(configuration.getJsonConverterSelector());
        assertNotNull(configuration.getJsonConverter());
    }


    @Test
    public void testCustomized() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        configuration.setRetryCount(3);
        configuration.setId("config_2");
        assertEquals("config_2", configuration.getId());
        assertEquals(Integer.valueOf(3), configuration.getRetryCount());
        configuration.setMaxConnections(123);
        assertEquals(Integer.valueOf(123), configuration.getMaxConnections());
        configuration.setMaxRouteConnections(222);
        assertEquals(Integer.valueOf(222), configuration.getMaxRouteConnections());
        configuration.setTimeout(12000);
        assertEquals(Integer.valueOf(12000), configuration.getTimeout());
    }

    @Test
    public void testVars() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("name", "Peter");
        configuration.setVariableValue("baseUrl", "http://abc.com");
        assertEquals("Peter", configuration.getVariableValue("name"));
        assertEquals("http://abc.com", configuration.getVariableValue("baseUrl"));

        Map<String, Object> varMap = new HashMap<>();
        varMap.put("name", "Linda");
        varMap.put("abc", "123");
        configuration.setVariables(varMap);
        assertEquals("Linda", configuration.getVariableValue("name"));
        assertEquals("123", configuration.getVariableValue("abc"));
        assertEquals(varMap, configuration.getVariables());
    }


    @Test
    public void testDefaultParameters() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        List<RequestNameValue> defaultParameters = new LinkedList<>();
        defaultParameters.add(new RequestNameValue("name", "Peter", false));
        defaultParameters.add(new RequestNameValue("age", "15", false));
        configuration.setDefaultParameters(defaultParameters);
        assertEquals(defaultParameters, configuration.getDefaultParameters());
    }

    @Test
    public void testDefaultHeaders() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        List<RequestNameValue> defaultHeaders = new LinkedList<>();
        defaultHeaders.add(new RequestNameValue("Accept", "text/html", false));
        configuration.setDefaultHeaders(defaultHeaders);
        assertEquals(defaultHeaders, configuration.getDefaultHeaders());
    }

    @Test
    public void testConverterMap() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        assertNotNull(configuration.getConverterMap());
        Map<ForestDataType, ForestConverter> converterMap = new HashMap<>();
        converterMap.put(ForestDataType.JSON, new ForestFastjsonConverter());
        configuration.setConverterMap(converterMap);
        assertEquals(converterMap, configuration.getConverterMap());
    }

    @Test
    public void testJSONCOnvertSelectCheck() throws ClassNotFoundException {
        JSONConverterSelector jsonConverterSelector = new JSONConverterSelector();
        Class fastJsonClass = jsonConverterSelector.checkFastJSONClass();
        Class gsonClass = jsonConverterSelector.checkGsonClass();
        Class jacson = jsonConverterSelector.checkJacsonClass();
        assertNotNull(fastJsonClass);
        assertNotNull(gsonClass);
        assertNotNull(jacson);
    }

    @Test
    public void testJSONConverterSelect() throws ClassNotFoundException {
        JSONConverterSelector jsonConverterSelector = new JSONConverterSelector();
        JSONConverterSelector spy = Mockito.spy(jsonConverterSelector);
        Mockito.when(spy.checkFastJSONClass())
                .thenThrow(new ClassNotFoundException("com.alibaba.fastjson.JSON"));

        ForestJsonConverter jsonConverter = spy.select();
        assertNotNull(jsonConverter);
        assertTrue(jsonConverter instanceof ForestJacksonConverter);

        Mockito.when(spy.checkJacsonClass())
                .thenThrow(new ClassNotFoundException("com.fasterxml.jackson.databind.ObjectMapper"));
        jsonConverter = spy.select();
        assertNotNull(jsonConverter);
        assertTrue(jsonConverter instanceof ForestGsonConverter);

        Mockito.when(spy.checkGsonClass())
                .thenThrow(new ClassNotFoundException("com.google.gson.JsonParser"));
        jsonConverter = spy.select();
        assertNull(jsonConverter);
    }

}