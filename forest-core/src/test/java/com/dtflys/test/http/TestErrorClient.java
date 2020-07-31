package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.mock.ErrorMockServer;
import com.dtflys.test.mock.GetMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestErrorClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestErrorClient.class);

    @Rule
    public ErrorMockServer server = new ErrorMockServer(this);

    private static ForestConfiguration configuration;

    private GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", ErrorMockServer.port);
    }


    public TestErrorClient(HttpBackend backend) {
        super(backend, configuration);
        getClient = configuration.createInstance(GetClient.class);
    }



    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testErrorGet() {
        AtomicReference<String> content = new AtomicReference<>(null);
        String result = getClient.errorGet((ex, request, response) -> {
            content.set(response.getContent());
        });
        String str = content.get();
        assertNotNull(str);
        log.info("response: " + str);
        assertEquals(ErrorMockServer.EXPECTED, str);
        assertEquals(ErrorMockServer.EXPECTED, result);
    }

    @Test
    public void testErrorGet2() {
        ForestResponse<String> response = getClient.errorGet2();
        assertNotNull(response);
        assertTrue(response.isError());
        String content = response.getContent();
        assertNotNull(content);
        log.info("response: " + content);
        assertEquals(ErrorMockServer.EXPECTED, content);
    }

    @Test
    public void testErrorGet3() {
        boolean hasError = false;
        try {
            Map result = getClient.errorGet3();
        } catch (ForestNetworkException ex) {
            hasError = true;
            int status = ex.getStatusCode(); // 获取请求响应状态码
            ForestResponse<Map> response = ex.getResponse(); // 获取Response对象
            String content = response.getContent(); // 获取未经序列化的请求响应内容
            assertEquals(400, status);
            assertNotNull(response);
            assertEquals(ErrorMockServer.EXPECTED, content);
        }
        assertTrue(hasError);
    }


}
