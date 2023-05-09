package org.dromara.test.http;

import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.backend.httpclient.HttpclientBackend;
import org.dromara.forest.backend.okhttp3.OkHttp3Backend;
import org.dromara.forest.config.ForestConfiguration;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-28 19:15
 */
@RunWith(Parameterized.class)
public abstract class BaseClientTest {

    protected HttpBackend backend;

    public BaseClientTest(HttpBackend backend, ForestConfiguration configuration) {
        this.backend = backend;
        configuration.setCacheEnabled(false);
        configuration.setBackend(backend);
    }

    @Parameterized.Parameters
    public static Collection backendList() {
        return Arrays.asList(
                new HttpBackend[][] {
                        {new HttpclientBackend()},
                        {new OkHttp3Backend()}});
    }

    @After
    public void afterRequests() {

    }

}
