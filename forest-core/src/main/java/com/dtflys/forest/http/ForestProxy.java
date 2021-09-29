package com.dtflys.forest.http;

import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;

import java.util.List;

/**
 * Forest请求正向代理
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.5.0-BETA5
 */
public class ForestProxy {

    private final String host;

    private final int port;

    private String username;

    private String password;

    private ForestHeaderMap headers = new ForestHeaderMap();

    public ForestProxy(String ip, int port) {
        this.host = ip;
        this.port = port;
    }

    /**
     * 获取代理主机地址
     *
     * @return 代理主机地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 获取代理主机端口
     *
     * @return 代理主机端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 获取代理用户名
     *
     * @return 代理用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置代理用户名
     *
     * @param username 代理用户名
     * @return {@link ForestProxy}对象实例
     */
    public ForestProxy setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * 获取代理密码
     *
     * @return 代理密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置代理密码
     *
     * @param password 代理密码
     * @return {@link ForestProxy}类实例
     */
    public ForestProxy setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * 获取该代理的所有请求头信息
     *
     * @return 请求头表，{@link ForestHeaderMap}类实例
     */
    public ForestHeaderMap getHeaders() {
        return headers;
    }

    /**
     * 根据请求头名称获取该代理的请求头信息
     *
     * @param name 请求头名称
     * @return 请求头，{@link ForestHeader}类实例
     */
    public ForestHeader getHeader(String name) {
        return headers.getHeader(name);
    }

    /**
     * 根据请求头名称获取该代理的请求头的值
     *
     * @param name 请求头名称
     * @return 请求头的值
     */
    public String getHeaderValue(String name) {
        return headers.getValue(name);
    }

    /**
     * 添加请求头到该代理中
     *
     * @param name 请求头名称
     * @param value 请求头的值
     * @return {@link ForestProxy}类实例
     */
    public ForestProxy addHeader(String name, Object value) {
        if (StringUtils.isEmpty(name)) {
            return this;
        }
        this.headers.setHeader(name, String.valueOf(value));
        return this;
    }

    /**
     * 添加请求头到该代理中
     *
     * @param nameValue 请求头键值对，{@link RequestNameValue}类实例
     * @return {@link ForestProxy}类实例
     */
    public ForestProxy addHeader(RequestNameValue nameValue) {
        this.addHeader(nameValue.getName(), nameValue.getValue());
        return this;
    }

    /**
     * 批量添加请求头到该代理中
     *
     * @param nameValues 请求头键值对列表
     * @return {@link ForestProxy}类实例
     */
    public ForestProxy addHeaders(List<RequestNameValue> nameValues) {
        for (RequestNameValue nameValue : nameValues) {
            this.addHeader(nameValue.getName(), nameValue.getValue());
        }
        return this;
    }


}


