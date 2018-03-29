package org.forest.test.http;

import org.forest.backend.HttpBackend;
import org.forest.config.ForestConfiguration;
import org.forest.ssl.SSLKeyStore;
import org.forest.test.http.client.PostClient;
import org.forest.test.http.client.WxHttpClient;
import org.forest.test.http.model.WxReverseRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-21 17:52
 */
public class TestWxHttpClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestWxHttpClient.class);


    private WxHttpClient wxHttpClient;

    private static ForestConfiguration configuration;

    public TestWxHttpClient(HttpBackend backend) {
        super(backend, configuration);
        wxHttpClient = configuration.createInstance(WxHttpClient.class);

    }

    @BeforeClass
    public static void prepare() {
        configuration = ForestConfiguration.configuration();
        SSLKeyStore sslKeyStore = new SSLKeyStore(
                "bla-weixin-keystore",
                "PKCS12",
                "apiclient_cert.p12",
                "1217660101",
                "1217660101");
        sslKeyStore.setProtocols(new String[] {"TLSv1"});
        configuration.registerKeyStore(sslKeyStore);
    }

    @Test
    public void testBlaReverse() {
        WxReverseRequest request = new WxReverseRequest();
        request.setAppid("wxbce29784bdd01454");
        request.setMch_id("1217660101");
        request.setOut_trade_no("SO0110080004112320");
        request.setNonce_str("1234567890abcdef");
        request.setSign("23d06860e8adb9d3bb86bc84236c0aed");
        String resp = wxHttpClient.blaReverse(request);
        assertNotNull(request);
        log.info("response: " + resp);

    }

}
