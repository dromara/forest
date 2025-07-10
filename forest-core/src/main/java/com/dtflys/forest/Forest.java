package com.dtflys.forest;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestFuture;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.BasicVariable;
import com.dtflys.forest.utils.VersionUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
public abstract class Forest {

    /**
     * 当前 Forest 版本号
     */
    public final static String VERSION = VersionUtil.getForestVersion();

    /**
     * 根据后端名称获取Forest后端对象
     * 
     * @param backendName 后端名称
     * @return Forest后端对象
     * @since v1.7.1
     */
    public static HttpBackend backend(String backendName) {
        return Forest.config().backend(backendName);
    }

    /**
     * 获取或创建全局默认配置，即 {@link ForestConfiguration} 对象
     * <p>全局默认配置的配置ID为 forestConfiguration
     *
     * @return {@link ForestConfiguration} 对象
     */
    public static ForestConfiguration config() {
        return ForestConfiguration.configuration();
    }

    /**
     * 根据配置ID获取或创建配置，即 {@link ForestConfiguration} 对象
     *
     * @param id 配置 ID
     * @return {@link ForestConfiguration} 对象
     */
    public static ForestConfiguration config(String id) {
        return ForestConfiguration.configuration(id);
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
     * 创建 GET 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @param args 参数列表
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#get(String, Object...)
     */
    public static ForestRequest<?> get(String url, Object ...args) {
        return config().get(url, args);
    }

    /**
     * 创建 POST 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#post(String)
     */
    public static ForestRequest<?> post(String url) {
        return config().post(url);
    }


    /**
     * 创建 POST 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @param args 参数列表
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#post(String, Object...)
     */
    public static ForestRequest<?> post(String url, Object ...args) {
        return config().post(url, args);
    }

    /**
     * 创建 PUT 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#put(String)
     */
    public static ForestRequest<?> put(String url) {
        return config().put(url);
    }


    /**
     * 创建 PUT 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @param args 参数列表
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#put(String, Object...)
     */
    public static ForestRequest<?> put(String url, Object ...args) {
        return config().put(url, args);
    }

    /**
     * 创建 DELETE 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#delete(String)
     */
    public static ForestRequest<?> delete(String url) {
        return config().delete(url);
    }

    /**
     * 创建 DELETE 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @param args 参数列表
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#delete(String, Object...)
     */
    public static ForestRequest<?> delete(String url, Object ...args) {
        return config().delete(url, args);
    }

    /**
     * 创建 HEAD 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#head(String)
     */
    public static ForestRequest<?> head(String url) {
        return config().head(url);
    }

    /**
     * 创建 HEAD 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @param args 参数列表
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#head(String, Object...)
     */
    public static ForestRequest<?> head(String url, Object ...args) {
        return config().head(url, args);
    }

    /**
     * 创建 PATCH 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#patch(String)
     */
    public static ForestRequest<?> patch(String url) {
        return config().patch(url);
    }

    /**
     * 创建 PATCH 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @param args 参数列表
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#patch(String, Object...)
     */
    public static ForestRequest<?> patch(String url, Object ...args) {
        return config().patch(url, args);
    }

    /**
     * 创建 OPTIONS 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#options(String)
     */
    public static ForestRequest<?> options(String url) {
        return config().options(url);
    }

    /**
     * 创建 OPTIONS 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @param args 参数列表
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#options(String)
     */
    public static ForestRequest<?> options(String url, Object ...args) {
        return config().options(url, args);
    }

    /**
     * 创建 TRACE 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#trace(String, Object...)
     */
    public static ForestRequest<?> trace(String url) {
        return config().trace(url);
    }

    /**
     * 创建 TRACE 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @param args 参数列表
     * @return {@link ForestRequest} 对象
     * @see ForestConfiguration#trace(String, Object...)
     */
    public static ForestRequest<?> trace(String url, Object ...args) {
        return config().trace(url, args);
    }

    /**
     * 等待多个请求响应结果，并阻塞当前调用改方法的线程，只有当参数中所有请求结果都返回后才会继续执行
     *
     * @param futures 可变参数 - 多个请求的 {@link ForestFuture} 对象
     * @return 请求返回后的 Forest 响应对象列表
     * @since 1.5.27
     */
    public static List<ForestResponse> await(ForestFuture ...futures) {
        return Arrays.stream(futures)
                .map(ForestFuture::await)
                .collect(Collectors.toList());
    }

    /**
     * 等待多个请求响应结果，并阻塞当前调用改方法的线程，只有当参数中所有请求结果都返回后才会继续执行
     *
     * @param futures 多个请求的 {@link ForestFuture} 对象集合
     * @return 请求返回后的 Forest 响应对象列表
     * @since 1.5.27
     */
    public static List<ForestResponse> await(Collection<ForestFuture> futures) {
        return futures.stream()
                .map(ForestFuture::await)
                .collect(Collectors.toList());
    }


    /**
     * 等待多个请求响应结果，并阻塞当前调用改方法的线程，当参数中所有请求结果都返回后调用参数中的 callback 回调函数
     *
     * @param futures 多个请求的 {@link ForestFuture} 对象集合
     * @param callback 回调函数，只有当参数中所有请求的响应都返回后才会被调用
     * @since 1.5.27
     */
    public static void await(Collection<ForestFuture> futures, Consumer<ForestResponse> callback) {

        for (ForestFuture future : futures) {
            future.await();
        }
        for (ForestFuture future : futures) {
            callback.accept(future.getResponse());
        }
    }

    /**
     * 通过Cookie名和Cookie值创建一个Cookie
     *
     * @param name Cookie名
     * @param value Cookie值
     * @return Forest Cookie
     * @since 1.7.0
     */
    public static ForestCookie cookie(String name, String value) {
        return ForestCookie.nameValue(name, value);
    }

    public static MappingTemplate template(String template) {
        return MappingTemplate.create(template);
    }

    /**
     * 创建一个基础常量类型的 Forest 变量
     *
     * @param value 常量值
     * @return {@link BasicVariable}实例
     * @since 1.7.4
     */
    public static BasicVariable constant(Object value) {
        return new BasicVariable(value);
    }

}
