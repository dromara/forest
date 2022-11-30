/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dtflys.forest.http;

import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Forest请求正向代理
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.5.0-BETA5
 */
public class ForestProxy implements HasURL {

    private final String host;

    private final int port;

    private String charset;

    private String username;

    private String password;

    private ForestHeaderMap headers = new ForestHeaderMap(this);

    public ForestProxy(String ip, int port) {
        this.host = ip;
        this.port = port;
    }

    public String cacheKey() {
        StringBuilder builder = new StringBuilder(",-x=");
        if (StringUtils.isNotEmpty(host)) {
            builder.append(host);
        }
        builder.append(":");
        builder.append(port);
        if (!headers.isEmpty()) {
            builder.append(":");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.append("h=").append(entry.getKey()).append(":").append(entry.getValue()).append(";");
            }
        }
        return builder.toString();
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

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
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


    @Override
    public ForestURL url() {
        return new ForestURLBuilder()
                .setHost(host)
                .setPort(port)
                .build();
    }
}


