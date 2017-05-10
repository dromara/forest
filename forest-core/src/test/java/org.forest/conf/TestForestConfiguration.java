package org.forest.conf;

import junit.framework.Assert;
import org.forest.config.ForestConfiguration;
import org.forest.converter.ForestConverter;
import org.forest.converter.JSONConverterSelector;
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

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-08 12:40
 */
public class TestForestConfiguration {

    @Test
    public void testDefault() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        Assert.assertEquals("forestConfiguration" + configuration.hashCode(),
                configuration.getId());
        Assert.assertEquals(Integer.valueOf(3000), configuration.getTimeout());
        Assert.assertEquals(Integer.valueOf(2000), configuration.getConnectTimeout());
        Assert.assertEquals(Integer.valueOf(500), configuration.getMaxConnections());
        Assert.assertEquals(Integer.valueOf(500), configuration.getMaxRouteConnections());
        Assert.assertNotNull(configuration.getJsonConverterSelector());
        Assert.assertNotNull(configuration.getJsonCoverter());
    }


    @Test
    public void testCustomized() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        configuration.setRetryCount(3);
        configuration.setId("config_2");
        Assert.assertEquals("config_2", configuration.getId());
        Assert.assertEquals(Integer.valueOf(3), configuration.getRetryCount());
        configuration.setMaxConnections(123);
        Assert.assertEquals(Integer.valueOf(123), configuration.getMaxConnections());
        configuration.setMaxRouteConnections(222);
        Assert.assertEquals(Integer.valueOf(222), configuration.getMaxRouteConnections());
        configuration.setTimeout(12000);
        Assert.assertEquals(Integer.valueOf(12000), configuration.getTimeout());
    }

    @Test
    public void testVars() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("name", "Peter");
        configuration.setVariableValue("baseUrl", "http://abc.com");
        Assert.assertEquals("Peter", configuration.getVariableValue("name"));
        Assert.assertEquals("http://abc.com", configuration.getVariableValue("baseUrl"));

        Map<String, Object> varMap = new HashMap<>();
        varMap.put("name", "Linda");
        varMap.put("abc", "123");
        configuration.setVariables(varMap);
        Assert.assertEquals("Linda", configuration.getVariableValue("name"));
        Assert.assertEquals("123", configuration.getVariableValue("abc"));
        Assert.assertEquals(varMap, configuration.getVariables());
    }


    @Test
    public void testDefaultParameters() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        List<RequestNameValue> defaultParameters = new LinkedList<>();
        defaultParameters.add(new RequestNameValue("name", "Peter"));
        defaultParameters.add(new RequestNameValue("age", "15"));
        configuration.setDefaultParameters(defaultParameters);
        Assert.assertEquals(defaultParameters, configuration.getDefaultParameters());
    }

    @Test
    public void testDefaultHeaders() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        List<RequestNameValue> defaultHeaders = new LinkedList<>();
        defaultHeaders.add(new RequestNameValue("Accept", "text/html"));
        configuration.setDefaultHeaders(defaultHeaders);
        Assert.assertEquals(defaultHeaders, configuration.getDefaultHeaders());
    }

    @Test
    public void testConverterMap() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        Assert.assertNotNull(configuration.getConverterMap());
        Map<ForestDataType, ForestConverter> converterMap = new HashMap<>();
        converterMap.put(ForestDataType.JSON, new ForestFastjsonConverter());
        configuration.setConverterMap(converterMap);
        Assert.assertEquals(converterMap, configuration.getConverterMap());
    }

    @Test
    public void testJSONConverterSelect() throws ClassNotFoundException {
        JSONConverterSelector jsonConverterSelector = new JSONConverterSelector();
        JSONConverterSelector spy = Mockito.spy(jsonConverterSelector);
        Mockito.when(spy.checkFastJSONClass())
                .thenThrow(new ClassNotFoundException("com.alibaba.fastjson.JSON"));

        ForestJsonConverter jsonConverter = spy.select();
        Assert.assertNotNull(jsonConverter);
        Assert.assertTrue(jsonConverter instanceof ForestJacksonConverter);

        Mockito.when(spy.checkJacsonClass())
                .thenThrow(new ClassNotFoundException("com.alibaba.fastjson.JSON"));
        jsonConverter = spy.select();
        Assert.assertNotNull(jsonConverter);
        Assert.assertTrue(jsonConverter instanceof ForestGsonConverter);

        Mockito.when(spy.checkGsonClass())
                .thenThrow(new ClassNotFoundException("com.google.gson.JsonParser"));
        jsonConverter = spy.select();
        Assert.assertNull(jsonConverter);
    }

}