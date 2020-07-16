package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.mock.PatchMockServer;
import com.dtflys.test.mock.PutMockServer;
import junit.framework.Assert;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.PatchClient;
import com.dtflys.test.mock.PatchMockServer;
import com.dtflys.test.mock.PutMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:13
 */
public class TestPatchClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPatchClient.class);

    @Rule
    public PatchMockServer server = new PatchMockServer(this);

    private static ForestConfiguration configuration;

    private static PatchClient patchClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", PatchMockServer.port);
    }

    public TestPatchClient(HttpBackend backend) {
        super(backend, configuration);
        patchClient = configuration.createInstance(PatchClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testSimplePatch() {
        String result = patchClient.simplePatch();
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PutMockServer.EXPECTED, result);
    }

    @Test
    public void testTextParamPatch() {
        String result = patchClient.textParamPatch("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PutMockServer.EXPECTED, result);
    }

    @Test
    public void testAnnParamPatch() {
        String result = patchClient.annParamPatch("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PutMockServer.EXPECTED, result);
    }


}
