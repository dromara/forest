package org.dromara.forest.backend;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.utils.StringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Forest后端框架选择器
 * <p>用于选择合适的后端的HTTP框架，Forest提供了两个默认的HTTP框架以供选择：
 * <ul>
 *     <li>httpclient</li>
 *     <li>okhttp3</li>
 * </ul>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-01 11:24
 */
public class HttpBackendSelector {
    private final static Map<String, HttpBackend> BACKEND_MAP = new ConcurrentHashMap<>();
    private final static Map<String, HttpBackendCreator> BACKEND_CREATOR_MAP = new ConcurrentHashMap<>();

    private final static String HTTPCLIENT_BACKEND_NAME = "httpclient";
    private final static String OKHTTP3_BACKEND_NAME = "okhttp3";

    public final static String HTTPCLIENT_CLIENT_CLASS_NAME = "org.apache.http.client.HttpClient";
    public final static String OKHTTP3_CLIENT_CLASS_NAME = "okhttp3.OkHttpClient";
    private final static String HTTPCLIENT_BACKEND_CLASS_NAME = "org.dromara.forest.backend.httpclient.HttpclientBackend";
    private final static String OKHTTP3_BACKEND_CLASS_NAME = "org.dromara.forest.backend.okhttp3.OkHttp3Backend";
    private final static HttpBackendCreator HTTPCLIENT_BACKEND_CREATOR = new HttpBackendCreator(HTTPCLIENT_BACKEND_CLASS_NAME);
    private final static HttpBackendCreator OKHTTP3_BACKEND_CREATOR = new HttpBackendCreator(OKHTTP3_BACKEND_CLASS_NAME);

    static {
        BACKEND_CREATOR_MAP.put(HTTPCLIENT_BACKEND_NAME, HTTPCLIENT_BACKEND_CREATOR);
        BACKEND_CREATOR_MAP.put(OKHTTP3_BACKEND_NAME, OKHTTP3_BACKEND_CREATOR);
    }

    /**
     * 获取所有已创建的Forest后端框架
     *
     * @return Map实例，Key: 后端框架名称, Value: {@link HttpBackend}接口实例
     */
    public Map<String, HttpBackend> getAllCreatedBackends() {
        return BACKEND_MAP;
    }

    /**
     * 根据全局配置选择Forest后端框架
     *
     * @param configuration Forest全局配置对象
     * @return Forest后端框架
     */
    public HttpBackend select(ForestConfiguration configuration) {
        String backendName = configuration.getBackendName();
        return select(backendName);
    }

    /**
     * 根据名称选择Forest后端框架
     *
     * @param backendName Forest后端框架名称，如：httpclient, okhttp3
     * @return Forest后端框架
     */
    public HttpBackend select(String backendName) {
        HttpBackend backend = null;
        if (StringUtil.isNotEmpty(backendName)) {
            backend = BACKEND_MAP.get(backendName);
        }
        if (backend == null) {
            synchronized (this) {
                if (StringUtil.isNotEmpty(backendName)) {
                    backend = BACKEND_MAP.get(backendName);
                }
                if (backend == null) {
                    if (StringUtil.isNotEmpty(backendName)) {
                        HttpBackendCreator backendCreator = BACKEND_CREATOR_MAP.get(backendName);
                        if (backendCreator == null) {
                            throw new ForestRuntimeException("Http setBackend \"" + backendName + "\" can not be found.");
                        }
                        backend = backendCreator.create();
                        if (backend != null) {
                            BACKEND_MAP.put(backendName, backend);
                            return backend;
                        }
                    }
                    backend = findOkHttp3BackendInstance();
                    if (backend != null) {
                        BACKEND_MAP.put(OKHTTP3_BACKEND_NAME, backend);
                        return backend;
                    }
                    backend = findHttpclientBackendInstance();
                    if (backend != null) {
                        BACKEND_MAP.put(HTTPCLIENT_BACKEND_NAME, backend);
                        return backend;
                    }
                    throw new ForestRuntimeException("Http Backed is undefined.");
                }
            }
        }
        return backend;
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
