package com.dtflys.forest.backend;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-01 11:24
 */
public class HttpBackendSelector {

    private final static Map<String, HttpBackendCreator> BACKEND_MAP = new HashMap<>();

    private final static String HTTPCLIENT_BACKEND_NAME = "httpclient";
    private final static String OKHTTP3_BACKEND_NAME = "okhttp3";

    public final static String HTTPCLIENT_CLIENT_CLASS_NAME = "org.apache.http.client.HttpClient";
    public final static String OKHTTP3_CLIENT_CLASS_NAME = "okhttp3.OkHttpClient";

    private final static String HTTPCLIENT_BACKEND_CLASS_NAME = "com.dtflys.forest.backend.httpclient.HttpclientBackend";
    private final static String OKHTTP3_BACKEND_CLASS_NAME = "com.dtflys.forest.backend.okhttp3.OkHttp3Backend";

    private final static HttpBackendCreator HTTPCLIENT_BACKEND_CREATOR = new HttpBackendCreator(HTTPCLIENT_BACKEND_CLASS_NAME);
    private final static HttpBackendCreator OKHTTP3_BACKEND_CREATOR = new HttpBackendCreator(OKHTTP3_BACKEND_CLASS_NAME);

    static {
        BACKEND_MAP.put(HTTPCLIENT_BACKEND_NAME, HTTPCLIENT_BACKEND_CREATOR);
        BACKEND_MAP.put(OKHTTP3_BACKEND_NAME, OKHTTP3_BACKEND_CREATOR);
    }

    public HttpBackend select(ForestConfiguration configuration) {
        String name = configuration.getBackendName();
        if (StringUtils.isNotEmpty(name)) {
            HttpBackendCreator backendCreator = BACKEND_MAP.get(name);
            if (backendCreator == null) {
                throw new ForestRuntimeException("Http setBackend \"" + name + "\" can not be found.");
            }
            return backendCreator.create();
        }

        HttpBackend backend = null;
        backend = findOkHttp3BackendInstance();
        if (backend != null) {
            return backend;
        }
        backend = findHttpclientBackendInstance();
        if (backend != null) {
            return backend;
        }
        throw new ForestRuntimeException("Http Backed is undefined.");
    }


    public HttpBackend findHttpclientBackendInstance() {
        try {
            Class.forName(HTTPCLIENT_CLIENT_CLASS_NAME);
            return HTTPCLIENT_BACKEND_CREATOR.create();
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    public HttpBackend findOkHttp3BackendInstance() {
        try {
            Class.forName(OKHTTP3_CLIENT_CLASS_NAME);
            return OKHTTP3_BACKEND_CREATOR.create();
        } catch (ClassNotFoundException e) {
        }
        return null;
    }


    static class HttpBackendCreator {

        public String className;

        public HttpBackendCreator(String className) {
            this.className = className;
        }

        public HttpBackend create() {
            try {
                Class klass = Class.forName(className);
                return (HttpBackend) klass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new ForestRuntimeException(e);
            } catch (InstantiationException e) {
                throw new ForestRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        }
    }

}
