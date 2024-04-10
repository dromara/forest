package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.httpclient.HttpclientBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.httpclient.HttpclientBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestFastjson2Converter;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import org.junit.After;
import org.junit.AfterClass;
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

    protected HttpBackend backend;

    protected ForestJsonConverter jsonConverter;

    public BaseClientTest(String backendName, String jsonConverterName, ForestConfiguration configuration) {
        this.backend = configuration.getBackendSelector().select(backendName);
        this.jsonConverter = selectJsonConverter(jsonConverterName);
        configuration.setCacheEnabled(false);
        configuration.setBackend(this.backend);
        configuration.setJsonConverter(this.jsonConverter);
    }

    private ForestJsonConverter selectJsonConverter(String converterName) {
        switch (converterName) {
            case "jackson":
                return new ForestJacksonConverter();
            case "fastjson":
                return new ForestFastjsonConverter();
            case "fastjson2":
                return new ForestFastjson2Converter();
        }
        throw new ForestRuntimeException(converterName + " dose not support");
    }

    @Parameterized.Parameters(name = " {index} : {0} - {1} ")
    public static Collection backendList() {
        return Arrays.asList(
                new Object[][] {
                        {"httpclient", "jackson"},
                        {"httpclient", "fastjson"},
                        {"httpclient", "fastjson2"},

                        {"okhttp3", "jackson"},
                        {"okhttp3", "fastjson"},
                        {"okhttp3", "fastjson2"}
                });
    }

    @After
    public void afterRequests() {

    }

}
