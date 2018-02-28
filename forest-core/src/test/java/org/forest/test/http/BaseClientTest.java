package org.forest.test.http;

import org.forest.backend.HttpBackend;
import org.forest.backend.httpclient.HttpclientBackend;
import org.forest.backend.okhttp3.OkHttp3Backend;
import org.forest.config.ForestConfiguration;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-28 19:15
 */
@RunWith(Parameterized.class)
public abstract class BaseClientTest {
    private final static Logger log = LoggerFactory.getLogger(BaseClientTest.class);

    protected HttpBackend backend;

    public BaseClientTest(HttpBackend backend, ForestConfiguration configuration) {
        this.backend = backend;
        configuration.setBackend(backend);
    }

    @Before
    public void logBackendName() {
        log.info("Backend: " + backend.getName());
    }

    @Parameterized.Parameters
    public static Collection backendList() {
        return Arrays.asList(
                new HttpBackend[][] {
                        {new HttpclientBackend()},
                        {new OkHttp3Backend()}});
    }

}
