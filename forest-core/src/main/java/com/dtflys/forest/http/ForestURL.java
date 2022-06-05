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

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Forest URL
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since v1.5.2
 */
public class ForestURL {

    /**
     * 原始URL
     * <p>即为整个完整的没被拆分的URL字符串
     */
    private String originalUrl;

    /**
     * HTTP协议
     */
    private String scheme;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 主机端口
     */
    private Integer port;

    /**
     * URL根路径
     */
    private String basePath;

    /**
     * URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     */
    private String path;

    /**
     * 用户信息
     *
     * <p>包含在URL中的用户信息，比如:
     * <p>URL http://xxx:yyy@localhost:8080 中 xxx:yyy 的部分为用户信息
     * <p>其中，xxx为用户名，yyy为用户密码
     */
    private String userInfo;

    /**
     * reference
     * <p>URL井号(#)后面的字符串
     */
    private String ref;

    /**
     * 是否为SSL
     */
    private boolean ssl;

    public ForestURL(URL url) {
        if (url == null) {
            throw new ForestRuntimeException("[Forest] Request url cannot be null!");
        }
        setScheme(url.getProtocol());
        host = url.getHost();
        port = url.getPort();
        path = url.getPath();
        userInfo = url.getUserInfo();
        setRef(url.getRef());
        originalUrl = toURLString();
    }

    public ForestURL(String schema, String userInfo, String host, Integer port, String path) {
        setScheme(schema);
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = path;
        originalUrl = toURLString();
    }

    /**
     * 获取原始URL
     *
     * @return 原始URL字符串
     */
    public String getOriginalUrl() {
        return originalUrl;
    }

    public ForestURL setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
        return this;
    }

    /**
     * 设置基础地址信息
     *
     * @param baseAddress {@link ForestAddress}对象
     * @return {@link ForestURL}对象
     */
    public ForestURL setBaseAddress(ForestAddress baseAddress) {
        if (baseAddress != null) {
            String baseScheme = baseAddress.getScheme();
            String baseHost = baseAddress.getHost();
            String bastPath = baseAddress.getBasePath();

            int basePort = baseAddress.getPort();
            setBasePath(bastPath);
            setScheme(baseScheme);
            setHost(baseHost);
            if (basePort != -1) {
                setPort(basePort);
            }
        }
        return this;
    }

    public String getScheme() {
        return scheme;
    }

    public ForestURL setScheme(String scheme) {
        if (StringUtils.isBlank(scheme)) {
            return this;
        }
        this.scheme = scheme.trim();
        this.ssl = "https".equals(this.scheme);
        this.originalUrl = toURLString();
        return this;
    }

    public String getHost() {
        return host;
    }

    public ForestURL setHost(String host) {
        if (StringUtils.isBlank(host)) {
            return this;
        }
        this.host = host.trim();
        if (this.host.endsWith("/")) {
            this.host = this.host.substring(0, this.host.lastIndexOf("/"));
        }
        this.originalUrl = toURLString();
        return this;
    }

    public int getPort() {
        if (URLUtils.isNonePort(port)) {
            return ssl ? 443 : 80;
        }
        return port;
    }

    public ForestURL setPort(int port) {
        this.port = port;
        this.originalUrl = toURLString();
        return this;
    }

    /**
     * 获取URL根路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @return URL根路径
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * 设置URL根路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @param basePath
     */
    public ForestURL setBasePath(String basePath) {
        if (basePath == null) {
            return this;
        }
        this.basePath = basePath.trim();
        if (!this.basePath.startsWith("/")) {
            if (URLUtils.isURL(this.basePath)) {
                try {
                    URL url = new URL(this.basePath);
                    this.scheme = url.getProtocol();
                    this.userInfo = url.getUserInfo();
                    this.host = url.getHost();
                    this.port = url.getPort();
                    this.basePath = url.getPath();
                } catch (MalformedURLException e) {
                    throw new ForestRuntimeException(e);
                }
            } else {
                this.basePath = "/" + this.basePath;
            }
        }
        this.originalUrl = toURLString();
        return this;
    }

    /**
     * 获取URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port + BasePath 后部分
     *
     * @return URL路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port + BasePath 后部分
     *
     * @param path URL路径
     * @return {@link ForestURL}对象实例
     */
    public ForestURL setPath(String path) {
        if (path == null) {
            return this;
        }
        this.path = path.trim();
        if (!this.path.startsWith("/")) {
            this.path = "/" + this.path;
        }
        this.originalUrl = toURLString();
        return this;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public ForestURL setUserInfo(String userInfo) {
        this.userInfo = userInfo;
        this.originalUrl = toURLString();
        return this;
    }

    public String getAuthority() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotEmpty(userInfo)) {
            builder.append(URLUtils.userInfoEncode(userInfo, "UTF-8")).append("@");
        }
        builder.append(URLUtils.userInfoEncode(host, "UTF-8"));
        if (URLUtils.isNotNonePort(port) &&
                ((port != 80 && port != 443 && port > -1) ||
                (port == 80 && !ssl) ||
                (port == 443 && !ssl))) {
            builder.append(':').append(port);
        }
        return builder.toString();
    }

    public String getRef() {
        return ref;
    }

    public ForestURL setRef(String ref) {
        this.ref = ref;
        if (StringUtils.isNotBlank(ref)) {
            try {
                this.ref = URLEncoder.encode(this.ref.trim(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return this;
    }

    public boolean isSSL() {
        return ssl;
    }

    public String toURLString() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotEmpty(scheme)) {
            builder.append(scheme).append("://");
        }
        String authority = getAuthority();
        if (StringUtils.isNotEmpty(authority)) {
            builder.append(authority);
        }
        if (StringUtils.isNotEmpty(basePath)) {
            builder.append(URLUtils.pathEncode(basePath, "UTF-8"));
        }
        if (StringUtils.isNotEmpty(path)) {
            builder.append(URLUtils.pathEncode(path, "UTF-8"));
        }
        if (StringUtils.isNotEmpty(ref)) {
            builder.append("#").append(ref);
        }
        return builder.toString();
    }

    /**
     * 获取URL对应的路由
     *
     * @return {@link ForestRoute}对象实例
     * @author gongjun [dt_flys@hotmail.com]
     * @since 1.5.22
     */
    public ForestRoute getRoute() {
        return ForestRoutes.getRoute(host, port);
    }

    @Override
    public String toString() {
        return originalUrl;
    }

    public URL toJavaURL() {
        try {
            return new URL(originalUrl);
        } catch (MalformedURLException e) {
            throw new ForestRuntimeException(e);
        }
    }

    public URI toURI() {
        try {
            return new URI(originalUrl);
        } catch (URISyntaxException e) {
            throw new ForestRuntimeException(e);
        }
    }

    /**
     * 合并两个URL
     * @param url 被合并的一个URL
     * @return 合并完的新URL
     */
    public ForestURL mergeURLWith(ForestURL url) {
        String newSchema = this.scheme == null ? url.scheme : this.scheme;
        String newUserInfo = this.userInfo == null ? url.userInfo : this.userInfo;
        String newHost = this.host == null ? url.host : this.host;
        int newPort = URLUtils.isNonePort(this.port) ? url.port : this.port;
        String newPath = this.path == null ? url.path : this.path;
        return new ForestURL(newSchema, newUserInfo, newHost, newPort, newPath);
    }




    /**
     * 设置基地址URL
     *
     * @param baseURL 基地址URL
     * @return {@link ForestURL}对象实例
     */
    public ForestURL setBaseURL(ForestURL baseURL) {
        String baseSchema = "http";
        String baseUserInfo = null;
        String baseHost = "localhost";
        int basePort = -1;
        String basePath = null;
        if (baseURL != null) {
            if (baseURL.scheme != null) {
                baseSchema = baseURL.scheme;
            }
            if (baseURL.userInfo != null) {
                baseUserInfo = baseURL.userInfo;
            }
            if (baseURL.host != null) {
                baseHost = baseURL.host;
            }
            if (URLUtils.isNotNonePort(baseURL.port)) {
                basePort = baseURL.port;
            }
            if (baseURL.path != null) {
                basePath = baseURL.path;
            }
        }
        boolean needBasePath = false;
        if (this.scheme == null) {
            this.scheme = baseSchema;
            needBasePath = true;
        }
        if (this.userInfo == null) {
            this.userInfo = baseUserInfo;
        }
        if (this.host == null) {
            this.host = baseHost;
            needBasePath = true;
        }

        if (URLUtils.isNonePort(this.port)) {
            this.port = basePort;
        }
        if (StringUtils.isNotBlank(this.path)) {
            if (this.path.charAt(0) != '/') {
                this.path = '/' + this.path;
            }
        }
        if (needBasePath && StringUtils.isNotBlank(basePath)) {
            if (basePath.charAt(basePath.length() - 1) == '/') {
                basePath = basePath.substring(0, basePath.length() - 1);
            }
            if (StringUtils.isEmpty(this.path)) {
                this.path = basePath;
            } else {
                this.path = basePath + this.path;
            }
        }
        this.originalUrl = toURLString();
        return this;
    }

}
