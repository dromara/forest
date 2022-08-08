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
import org.bouncycastle.crypto.tls.URLAndHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Forest URL
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since v1.5.2
 */
public class ForestURL {

    private final static Logger log = LoggerFactory.getLogger(ForestURL.class);

    /**
     * 原始URL
     * <p>即为整个完整的没被拆分的URL字符串
     */
    private String originalUrl;

    private ForestAddress address;

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

    public ForestURL(String scheme, String userInfo, String host, Integer port, String path) {
        this(scheme, userInfo, host, port, path, null);
    }


    public ForestURL(String scheme, String userInfo, String host, Integer port, String path, String ref) {
        setScheme(scheme);
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = path;
        this.ref = ref;
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
        if (StringUtils.isEmpty(scheme) && address != null) {
            return address.getScheme();
        }
        if (StringUtils.isEmpty(scheme)) {
            return ssl ? "https" : "http";
        }
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
        if (StringUtils.isEmpty(host) && address != null) {
            return address.getHost();
        }
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

    private static int normalizePort(Integer port, boolean ssl) {
        if (URLUtils.isNonePort(port)) {
            return ssl ? 443 : 80;
        }
        return port;
    }

    public int getPort() {
        if (URLUtils.isNonePort(port) && address != null) {
            return normalizePort(address.getPort(), ssl);
        }
        return normalizePort(port, ssl);
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
        if (StringUtils.isNotEmpty(basePath) && basePath.charAt(0) != '/') {
            return '/' + basePath;
        }
        return basePath;
    }


    /**
     * 设置URL根路径 (强制修改)
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @param basePath 根路径
     * @return {@link ForestURL}对象实例
     */
    public ForestURL setBasePath(String basePath) {
        return setBasePath(basePath, true);
    }

    /**
     * 设置URL根路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @param basePath 根路径
     * @param forced 是否强制修改, {@code true}: 强制修改非根路径部分地址信息, {@code false}: 非强制，如果URL已设置host、port等非根路径部分地址信息则不会修改
     * @return {@link ForestURL}对象实例
     */
    public ForestURL setBasePath(String basePath, boolean forced) {
        if (basePath == null) {
            return this;
        }
        this.basePath = basePath.trim();
        if (!this.basePath.startsWith("/")) {
            if (URLUtils.isURL(this.basePath)) {
                try {
                    String originHost = this.host;
                    URL url = new URL(this.basePath);
                    if (forced || StringUtils.isEmpty(this.scheme)) {
                        setScheme(url.getProtocol());
                    }
                    if (forced || StringUtils.isEmpty(this.userInfo)) {
                        this.userInfo = url.getUserInfo();
                    }
                    if (forced || StringUtils.isEmpty(this.host)) {
                        this.host = url.getHost();
                    }
                    if (forced || (URLUtils.isNonePort(port) && StringUtils.isEmpty(originHost))) {
                        this.port = url.getPort();
                    }
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
        if (StringUtils.isNotEmpty(path) && path.charAt(0) != '/') {
            return '/' + path;
        }
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
        if (StringUtils.isNotEmpty(host)) {
            builder.append(URLUtils.userInfoEncode(host, "UTF-8"));
        }
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
            String encodedBasePath = URLUtils.pathEncode(basePath, "UTF-8");
            if (host != null && encodedBasePath.charAt(0) != '/') {
                builder.append('/');
            }
            builder.append(encodedBasePath);
        }
        if (StringUtils.isNotEmpty(path)) {
            String encodedPath = URLUtils.pathEncode(path, "UTF-8");
            if ((host != null || basePath != null) && encodedPath.charAt(0) != '/') {
                builder.append('/');
            }
            builder.append(encodedPath);
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
        return ForestRoutes.getRoute(getHost(), getPort());
    }

    @Override
    public String toString() {
        if (StringUtils.isNotEmpty(ref)) {
            return originalUrl + "#" +originalUrl;
        }
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
     * 修改地址信息 (强制修改)
     *
     * @param address 地址, {@link ForestAddress}对象实例
     * @return {@link ForestURL}对象实例
     */
    public ForestURL setAddress(ForestAddress address) {
        return setAddress(address, true);
    }

    /**
     * 修改地址信息
     *
     * @param address 地址, {@link ForestAddress}对象实例
     * @param forced 是否强制修改, {@code true}: 强制修改, {@code false}: 非强制，如果URL已设置host、port等信息则不会修改
     * @return {@link ForestURL}对象实例
     */
    public ForestURL setAddress(ForestAddress address, boolean forced) {
        if (forced) {
            setBaseAddress(address);
        } else {
            this.address = address;
        }
        return this;
    }


    /**
     * 合并两个URL
     * @param url 被合并的一个URL
     * @return 合并完的新URL
     */
    public ForestURL mergeURLWith(ForestURL url) {
        if (url == null) {
            return this;
        }
        String newSchema = this.scheme == null ? url.scheme : this.scheme;
        String newUserInfo = this.userInfo == null ? url.userInfo : this.userInfo;
        String newHost = this.host == null ? url.host : this.host;
        Integer newPort = this.port == null ? url.port : this.port;
        String newPath = this.path == null ? url.path : this.path;
        String newRef = this.ref == null ? url.ref : this.ref;
        return new ForestURL(newSchema, newUserInfo, newHost, newPort, newPath, newRef);
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
            setScheme(baseSchema);
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

    public ForestURL mergeAddress() {
        if (address != null) {
            String originHost = host;
            if (StringUtils.isEmpty(scheme)) {
                scheme = address.getScheme();
            }
            if (StringUtils.isEmpty(host)) {
                host = address.getHost();
            }
            if (URLUtils.isNonePort(port) && StringUtils.isEmpty(originHost)) {
                port = address.getPort();
            }
            if (StringUtils.isEmpty(basePath)) {
                setBasePath(address.getBasePath(), false);
            }
            originalUrl = toURLString();
        }
        return this;
    }

    public ForestURL checkAndComplete() {
        String oldUrl = originalUrl;
        if (StringUtils.isEmpty(scheme)) {
            setScheme(ssl ? "https" : "http");
        }
        if (StringUtils.isEmpty(host)) {
            setHost("localhost");
            if (URLUtils.isNonePort(port)) {
                log.warn("[Forest] Invalid url '" + oldUrl + "'. But an valid url must start width 'http://' or 'https://'. Convert this url to '" + toURLString() + "' automatically!");
            } else {
                log.warn("[Forest] Invalid url '" + oldUrl + "'. Host is empty. Convert this url to '" + toURLString() + "' automatically!");
            }
        }
        return this;
    }

}
