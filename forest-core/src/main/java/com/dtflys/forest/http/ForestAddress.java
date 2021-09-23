package com.dtflys.forest.http;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import scala.Int;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Forest主机地址信息
 * <p>该地址信息为主机名/域名/ip地址 + 端口号的组合
 */
public class ForestAddress {

    /**
     * HTTP协议头
     */
    private final String scheme;

    /**
     * 主机地址(主机名/ip地址)
     */
    private final String host;

    /**
     * 主机端口号
     */
    private final int port;

    /**
     * 实例化Forest主机地址信息
     *
     * @param scheme HTTP协议头
     * @param host 主机地址(主机名/ip地址)
     * @param port 主机端口号，如果为 -1， 代表未设置端口号
     */
    public ForestAddress(String scheme, String host, int port) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
    }

    /**
     * 实例化Forest主机地址信息
     *
     * @param scheme HTTP协议头
     * @param host 主机地址(主机名/ip地址)
     * @param port 主机端口号
     */
    public ForestAddress(String scheme, String host, Integer port) {
        this(scheme, host, port == null ? -1 : port);
    }

    /**
     * 实例化Forest主机地址信息
     * <p>HTTP协议头默认为 http
     *
     * @param host 主机地址(主机名/ip地址)
     * @param port 主机端口号，如果为 -1， 代表未设置端口号
     */
    public ForestAddress(String host, int port) {
        this(null, host, port);
    }

    /**
     * 实例化Forest主机地址信息
     * <p>HTTP协议头默认为 http
     *
     * @param host 主机地址(主机名/ip地址)
     * @param port 主机端口号
     */
    public ForestAddress(String host, Integer port) {
        this(null, host, port);
    }


    /**
     * 获取HTTP协议头
     * @return HTTP协议头字符串
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * 获取主机地址
     * <p>可以为主机名或IP地址
     *
     * @return 主机地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 获取主机地址(主机名/ip地址)
     *
     * @return 主机端口号
     */
    public int getPort() {
        return port;
    }


}
