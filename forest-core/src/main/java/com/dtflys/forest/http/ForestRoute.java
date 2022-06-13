package com.dtflys.forest.http;

import com.dtflys.forest.utils.TimeUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Forest请求路由
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.22
 */
public class ForestRoute {

    /**
     * 主机名
     */
    private final String host;

    /**
     * 端口号
     */
    private final int port;

    /**
     * 后端客户端对象缓存
     */
    private final Map<String, Object> backendClientCache = new ConcurrentHashMap<>();

    /**
     * 当前的路由请求数
     */
    private final AtomicInteger requestCount = new AtomicInteger(0);

    public ForestRoute(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 获取主机名
     *
     * @return 主机名
     */
    public String getHost() {
        return host;
    }

    /**
     * 获取端口号
     *
     * @return 端口号
     */
    public int getPort() {
        return port;
    }

    /**
     * 获取当前的路由请求数
     *
     * @return 请求数
     */
    public AtomicInteger getRequestCount() {
        return requestCount;
    }

    public static String domain(String host, int port) {
        StringBuilder builder = new StringBuilder(host);
        if (port != 80 && port != -1) {
            builder.append(":").append(port);
        }
        return builder.toString();
    }

    /**
     * 获取域名
     *
     * @return 域名
     */
    public String getDomain() {
        return domain(host, port);
    }



    /**
     * 从缓存中获取后端客户端对象
     *
     * @param key 缓存的Key
     * @param <T> 后端客户端对象类型
     * @return 后端客户端对象
     */
    public <T> T getBackendClient(String key) {
        Object client = backendClientCache.get(key);
        if (client != null) {
            return (T) client;
        }
        return null;
    }

    /**
     * 缓存后端客户端对象
     *
     * @param key 缓存的Key
     * @param client 后端客户端对象
     */
    public void cacheBackendClient(String key, Object client) {
        backendClientCache.put(key, client);
    }
}
