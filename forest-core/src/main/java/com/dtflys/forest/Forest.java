package com.dtflys.forest;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * Forest 快捷接口
 *
 * <p>该类提供 Forest 常用的基本接口方法, 列如:
 * <pre>
 *     // 获取 Forest GET请求
 *     Forest.get("http://localhost:8080")
 *
 *     // 获取 Forest POST请求
 *     Forest.post("http://localhost:8080")
 *
 *     // 创建或获取全局默认配置，即 ForestConfiguration 对象
 *     Forest.config();
 * </pre>
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
public class Forest {

    private volatile static ForestConfiguration DEFAULT_CONFIG;

    /**
     * 创建或获取全局默认配置，即 {@link ForestConfiguration} 对象
     *
     * @return {@link ForestConfiguration} 对象
     */
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
     * @return {@link ForestRequest} 对象
     */
    public static ForestRequest<?> request() {
        return config().request();
    }

    /**
     * 创建通用 {@link ForestRequest} 对象
     *
     * @param clazz 返回结果类型
     * @param <R> 返回结果类型泛型参数
     * @return {@link ForestRequest} 对象
     */
    public static <R> ForestRequest<R> request(Class<R> clazz) {
        return config().request(clazz);
    }


    /**
     * 创建 GET 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#get(String)
     */
    public static ForestRequest<?> get(String url) {
        return config().get(url);
    }

    /**
     * 创建 POST 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#get(String)
     */
    public static ForestRequest<?> post(String url) {
        return config().post(url);
    }

    /**
     * 创建 PUT 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#get(String)
     */
    public static ForestRequest<?> put(String url) {
        return config().put(url);
    }

    /**
     * 创建 DELETE 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#get(String)
     */
    public static ForestRequest<?> delete(String url) {
        return config().delete(url);
    }

    /**
     * 创建 HEAD 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#get(String)
     */
    public static ForestRequest<?> head(String url) {
        return config().head(url);
    }

    /**
     * 创建 PATCH 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#get(String)
     */
    public static ForestRequest<?> patch(String url) {
        return config().patch(url);
    }

    /**
     * 创建 OPTIONS 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#get(String)
     */
    public static ForestRequest<?> options(String url) {
        return config().options(url);
    }

    /**
     * 创建 TRACE 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#get(String)
     */
    public static ForestRequest<?> trace(String url) {
        return config().trace(url);
    }

}
