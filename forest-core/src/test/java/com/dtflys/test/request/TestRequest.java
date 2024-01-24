package com.dtflys.test.request;

import cn.hutool.core.util.ObjectUtil;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.InterceptorAttributes;
import com.dtflys.forest.mapping.ForestRequestContext;
import com.dtflys.test.interceptor.BasicAuthClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-05-09 23:10
 */
public class TestRequest {

    @Test
    public void testDefaultRequest() {
        ObjectUtil.isBasicType(Object.class);
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        ForestRequest request = new ForestRequest(configuration, new ForestRequestContext(configuration.getVariableContext(), null));
        assertEquals(configuration, request.getConfiguration());
        assertEquals(configuration.getTimeout().intValue(),
                request.getTimeout());
        assertEquals(0, request.getRetryCount());
    }


    @Test
    public void testInterceptorAttribute() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        ForestRequest request = new ForestRequest(configuration, new ForestRequestContext(configuration.getVariableContext(), null));
        request.interceptorAttribute(BasicAuthClient.class, "Xxx", "foo");
        request.interceptorAttribute(BasicAuthClient.class, "Yyy", "bar");
        Object xxxValue = request.getInterceptorAttribute(BasicAuthClient.class, "Xxx");
        assertNotNull(xxxValue);
        assertEquals("foo", xxxValue);

        Object yyyValue = request.getInterceptorAttribute(BasicAuthClient.class, "Yyy");
        assertNotNull(yyyValue);
        assertEquals("bar", yyyValue);

        Map<String, Object> attrMap = new HashMap<>();
        attrMap.put("Xxx", "xxxx");
        attrMap.put("Zzz", 1111);
        InterceptorAttributes attributes = new InterceptorAttributes(BasicAuthClient.class, attrMap);
        request.interceptorAttributes(BasicAuthClient.class, attributes);
        xxxValue = request.getInterceptorAttribute(BasicAuthClient.class, "Xxx");
        assertNotNull(xxxValue);
        assertEquals("xxxx", xxxValue);

        Object zzzValue = request.getInterceptorAttribute(BasicAuthClient.class, "Zzz");
        assertNotNull(zzzValue);
        assertEquals(Integer.valueOf(1111), zzzValue);
    }

    @Test
    public void testAttachment() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        ForestRequest request = new ForestRequest(configuration, new ForestRequestContext(configuration.getVariableContext(), null));
        request.attachment("Xxx", "foo");
        request.attachment("Yyy", "bar");
        Object xxxValue = request.getAttachment("Xxx");
        Object yyyValue = request.getAttachment("Yyy");
        assertEquals("foo", xxxValue);
        assertEquals("bar", yyyValue);
        request.attachment("Yyy", "1111");
        yyyValue = request.getAttachment("Yyy");
        assertEquals("1111", yyyValue);
    }

}
