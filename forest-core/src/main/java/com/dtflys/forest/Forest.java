package com.dtflys.forest;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;

public class Forest {

    private volatile static ForestConfiguration DEFAULT_CONFIG;

    public static ForestConfiguration config() {
        if (DEFAULT_CONFIG == null) {
            synchronized (Forest.class) {
                DEFAULT_CONFIG = ForestConfiguration.configuration();
            }
        }
        return DEFAULT_CONFIG;
    }

    /**
     * 创建 Forest 客户端接口实例
     *
     * @param clazz  请求接口类
     * @param <T>    请求接口类泛型
     * @return       Forest 接口实例
     */
    public static <T> T client(Class<T> clazz) {
        return config().createInstance(clazz);
    }

    /**
     * 创建通用 {@link ForestRequest} 对象
     *
     * @param <T> 请求响应返回后的接受类型
     * @return {@link ForestRequest} 对象
     */
    public static <T> ForestRequest<T> request() {
        return config().request();
    }

    /**
     * 创建 GET 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @param <T> 请求响应返回后的接受类型
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#get(String)
     */
    public static <T> ForestRequest<T> get(String url) {
        return config().get(url);
    }

}
