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

import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.MappingListener;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.MappingURLTemplate;
import com.dtflys.forest.reflection.BasicVariable;
import com.dtflys.forest.reflection.ForestVariable;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLUtils;
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
 * @since 1.5.2
 */
public class ForestURL implements MappingListener {

    private final static Logger log = LoggerFactory.getLogger(ForestURL.class);

    protected volatile ForestRequest request;

    protected ForestURL mergedURL;

    volatile ForestURL baseURL;

    /**
     * 解析后的URL
     * <p>即为整个完整的没被拆分的URL字符串
     */
    protected String parsedUrl;

    protected volatile ForestAddress baseAddress;

    protected volatile ForestAddress address;


    /**
     * HTTP协议
     */
    protected volatile ForestVariable scheme;

    /**
     * 主机地址
     */
    protected volatile ForestVariable host;

    /**
     * 主机端口
     */
    protected volatile ForestVariable port;

    /**
     * URL根路径
     */
    protected String basePath;

    /**
     * URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     */
    protected volatile ForestVariable path;

    /**
     * 用户信息
     *
     * <p>包含在URL中的用户信息，比如:
     * <p>URL http://xxx:yyy@localhost:8080 中 xxx:yyy 的部分为用户信息
     * <p>其中，xxx为用户名，yyy为用户密码
     */
    protected String userInfo;

    /**
     * reference
     * <p>URL井号(#)后面的字符串
     */
    protected ForestVariable ref;

    protected ForestQueryMap query = new ForestQueryMap(null);

    /**
     * 是否为SSL
     */
    protected boolean ssl;

    /**
     * 是否需要重新生成 URL
     */
    protected volatile boolean needUrlRegenerate = false;


    protected volatile boolean variablesChanged = false;

    protected void needUrlRegenerate() {
        needUrlRegenerate = true;
    }

    public static ForestURL create(final String url) {
        return create(Forest.config(), url);
    }


    public static ForestURL create(ForestRequest request, final MappingURLTemplate template) {
        return newURL(request, null, template, false);
    }


    public static ForestURL create(final MappingURLTemplate template) {
        return newURL(null, template);
    }


    public static ForestURL create(final ForestConfiguration configuration, final String url) {
        final MappingURLTemplate urlTemplate = MappingURLTemplate.get(configuration, url);
        final ForestURL forestURL = newURL(url, urlTemplate);
        return forestURL;
    }

    public static ForestURL parse(final ForestRequest request, final String url) {
        if (request == null) {
            return parse(url);
        }
        final ForestConfiguration configuration = request.getConfiguration();
        final MappingURLTemplate urlTemplate = MappingURLTemplate.get(configuration, url);
        final ForestURL forestURL = newURL(request, url, urlTemplate, true);
        return forestURL;
    }

    private static ForestURL newURL(final String url, final MappingURLTemplate urlTemplate) {
        return newURL(null, url, urlTemplate, false);
    }

    private static ForestURL newURL(final ForestRequest request, final String url, final MappingURLTemplate urlTemplate, final boolean renderURL) {
        if (urlTemplate.isConstant()) {
            final ForestURL forestURL;
            try {
                forestURL = StringUtils.isNotEmpty(url) ? new ForestURL(new URL(url)) : new ForestURL();
            } catch (MalformedURLException e) {
                throw new ForestRuntimeException(e);
            }
//            forestURL.parsedUrl = url;
            forestURL.needUrlRegenerate = true;
            forestURL.variablesChanged = true;
            if (request != null) {
                urlTemplate.render(forestURL, request, request.arguments(), forestURL.query);
            }
            return forestURL;
        }
        return new ForestDynamicURL(urlTemplate);
    }

    public static ForestURL parse(final String url) {
        return parse(Forest.config(), url);
    }


    public static ForestURL parse(final ForestConfiguration configuration, final String url) {
        final MappingURLTemplate urlTemplate = MappingURLTemplate.get(configuration, url);
        final ForestURL forestURL = new ForestURL();
        forestURL.parsedUrl = url;
//        final ForestQueryMap queryMap = new ForestQueryMap(null);
        urlTemplate.render(forestURL, configuration, new Object[0], forestURL.query);
        return forestURL;
    }

    public static ForestURL fromUrl(String url) {
        try {
            return new ForestURL(new URL(url));
        } catch (MalformedURLException e) {
            throw new ForestRuntimeException(e);
        }
    }

    public static ForestURL unparsedURL(final String url) {
        ForestURL forestURL = new ForestURL();
        return forestURL;
    }

    public static ForestURL emptyURL() {
        return new ForestURL();
    }


    protected ForestURL() {
    }

    public ForestURL(URL url) {
        if (url == null) {
            throw new ForestRuntimeException("[Forest] Request url cannot be null!");
        }
        setScheme(url.getProtocol());
        host = ForestVariable.create(url.getHost());
        port = ForestVariable.create(url.getPort());
        path = ForestVariable.create(url.getPath());
        userInfo = url.getUserInfo();
        setRef(url.getRef());
    }

    public ForestURL(ForestVariable scheme, String userInfo, ForestVariable host, ForestVariable port, ForestVariable path) {
        this(scheme, userInfo, host, port, path, null);
    }


    public ForestURL(ForestVariable scheme, String userInfo, ForestVariable host, ForestVariable port, ForestVariable path, ForestVariable ref) {
        setScheme(scheme);
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = path;
        this.ref = ref;
        needUrlRegenerate();
    }
    
    private ForestURL(ForestURL mergedURL, ForestVariable scheme, String userInfo, ForestVariable host, ForestVariable port, ForestVariable path, ForestVariable ref) {
        this(scheme, userInfo, host, port, path, ref);
        if (mergedURL != null) {
            this.mergedURL = mergedURL;
            this.baseURL = mergedURL.baseURL;
            this.baseAddress = mergedURL.baseAddress;
            this.address = mergedURL.address;
            mergedURL.baseURL = null;
            mergedURL.baseAddress = null;
            mergedURL.address = null;
        }
    }


    void setRequest(final ForestRequest request) {
        this.request = request;
        if (baseURL != null) {
            baseURL.setRequest(request);
        }
    }

    /**
     * 获取原始URL
     *
     * @return 原始URL字符串
     */
    public String getOriginalUrl() {
        if (parsedUrl == null || needUrlRegenerate) {
            parsedUrl = toURLString();
            needUrlRegenerate = false;
        }
        return parsedUrl;
    }

    public ForestQueryMap getQuery() {
        return query;
    }

    /**
     * 设置基础地址信息
     *
     * @param baseAddress {@link ForestAddress}对象
     * @return {@link ForestURL}对象
     */
    public ForestURL setBaseAddress(ForestAddress baseAddress) {
        this.baseAddress = baseAddress;
        return this;
    }

    public String getScheme() {
        final String schemeStr = getSchemeWithoutBaseURL();
        if (StringUtils.isNotEmpty(schemeStr)) {
            return normalizeScheme(schemeStr);
        }
        if (address != null) {
            final String addressScheme = address.getScheme();
            if (StringUtils.isNotEmpty(addressScheme)) {
                return normalizeScheme(addressScheme);
            }
        }
        if (baseURL != null) {
            final String baseScheme = baseURL.getScheme();
            if (StringUtils.isNotEmpty(baseScheme)) {
                return normalizeScheme(baseScheme);
            }
        }
        if (baseAddress != null) {
            final String baseAddressScheme = baseAddress.getScheme();
            if (StringUtils.isNotEmpty(baseAddressScheme)) {
                return normalizeScheme(baseAddressScheme);
            }
        }
        if (mergedURL != null) {
            mergedURL.ssl = ssl;
            final String mergedScheme = mergedURL.getSchemeWithoutBaseURL();
            if (StringUtils.isNotEmpty(mergedScheme)) {
                return normalizeScheme(mergedScheme);
            }
        }
        return normalizeScheme(schemeStr);
    }

    private void refreshSSL() {
        final String schemeStr = ForestVariable.getStringValue(scheme, request);
        this.ssl = "https".equals(schemeStr);
    }

    private void refreshSSL(final String schemeStr) {
        this.ssl = "https".equals(schemeStr);
    }

    protected String normalizeScheme(final String scheme) {
        final String schemeStr = StringUtils.isEmpty(scheme) ? (ssl ? "https" : "http") : scheme;
        refreshSSL(schemeStr);
        needUrlRegenerate();
        return schemeStr;
    }

    public ForestURL setScheme(String scheme) {
        if (StringUtils.isBlank(scheme)) {
            return this;
        }
        this.scheme = ForestVariable.create(scheme.trim());
        return this;
    }

    public ForestURL setScheme(ForestVariable scheme) {
        this.scheme = scheme;
        return this;
    }


    protected String getHost(String host) {
        if (StringUtils.isEmpty(host) && address != null) {
            return address.getHost();
        }
        return host;
    }

    public String getHost() {
        final String hostStr = getHost(ForestVariable.getStringValue(host, request));
        if (StringUtils.isNotEmpty(hostStr)) {
            return hostStr;
        }
        if (address != null) {
            final String addressHost = address.getHost();
            if (StringUtils.isNotEmpty(addressHost)) {
                return addressHost;
            }
        }
        if (baseURL != null) {
            final String baseHost = baseURL.getHost();
            if (StringUtils.isNotEmpty(baseHost)) {
                return baseHost;
            }
        }
        if (baseAddress != null) {
            final String baseAddressHost = baseAddress.getHost();
            if (StringUtils.isNotEmpty(baseAddressHost)) {
                return baseAddressHost;
            }
        }
        if (mergedURL != null) {
            return mergedURL.getHost();
        }
        return hostStr;
    }

    public ForestURL setHost(String host) {
        if (StringUtils.isBlank(host)) {
            return this;
        }
        final String hostStr = host.trim();
        if (hostStr.endsWith("/")) {
            this.host = ForestVariable.create(hostStr.substring(0, hostStr.lastIndexOf("/")));
        } else {
            this.host = ForestVariable.create(hostStr);
        }
        needUrlRegenerate();
        return this;
    }

    public ForestURL setHost(ForestVariable hostVar) {
        if (hostVar == null) {
            return this;
        }
        if (hostVar instanceof BasicVariable) {
            final String hostStr = String.valueOf(((BasicVariable) hostVar).getValue());
            if (StringUtils.isBlank(hostStr)) {
                return this;
            }
            if (hostStr.endsWith("/")) {
                this.host = ForestVariable.create(hostStr.substring(0, hostStr.lastIndexOf("/")));
            } else {
                this.host = hostVar;
            }
        } else {
            this.host = hostVar;
        }
        needUrlRegenerate();
        return this;
    }


    protected static int normalizePort(Integer port, boolean ssl) {
        if (URLUtils.isNonePort(port)) {
            return ssl ? 443 : 80;
        }
        return port;
    }
    
    protected Integer getPortWithoutBaseURL() {
        return ForestVariable.getIntegerValue(port, request);
    }

    public int getPort() {
        final Integer portInt = getPortWithoutBaseURL();
        if (URLUtils.isNotNonePort(portInt)) {
            return normalizePort(portInt, ssl);
        }
        final String schemeStr = ForestVariable.getStringValue(scheme, request);
        if (StringUtils.isNotEmpty(schemeStr)) {
            return normalizePort(portInt, ssl);
        }

        if (address != null) {
            int addressPort = address.getPort();
            if (URLUtils.isNotNonePort(addressPort)) {
                return normalizePort(addressPort, ssl);
            }
        }
        if (baseURL != null) {
            final int basePort = baseURL.getPort();
            if (URLUtils.isNotNonePort(basePort)) {
                return basePort;
            }
        }

        if (baseAddress != null) {
            final int baseAddressPort = baseAddress.getPort();
            if (URLUtils.isNotNonePort(baseAddressPort)) {
                return normalizePort(baseAddressPort, ssl);
            }
        }
        if (mergedURL != null) {
            final Integer mergedPort = mergedURL.getPortWithoutBaseURL();
            if (URLUtils.isNotNonePort(mergedPort)) {
                return normalizePort(mergedPort, ssl);
            }
        }
        return normalizePort(portInt, ssl);
    }

    public ForestURL setPort(Integer port) {
        this.port = ForestVariable.create(port);
        needUrlRegenerate();
        return this;
    }

    public ForestURL setPort(String port) {
        this.port = ForestVariable.create(port);
        needUrlRegenerate();
        return this;
    }

    public ForestURL setPort(ForestVariable port) {
        this.port = port;
        needUrlRegenerate();
        return this;
    }

    /**
     * 获取URL根路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @return URL根路径
     */
    public String normalizeBasePath() {
        if (StringUtils.isEmpty(basePath)) {
            return normalizeBasePath(address.getBasePath());
        }
        return normalizeBasePath(basePath);
    }


    private String normalizeBasePath(String basePath) {
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
    

    protected String getBasePath() {
        if (StringUtils.isNotEmpty(basePath)) {
            return basePath;
        }
        if (address != null) {
            final String addressBasePath = address.getBasePath();
            if (StringUtils.isNotEmpty(addressBasePath)) {
                return addressBasePath;
            }
        }
        if (baseAddress != null) {
            final String baseAddressBasePath = baseAddress.getBasePath();
            if (StringUtils.isNotEmpty(baseAddressBasePath)) {
                return baseAddressBasePath;
            }
        }
        if (mergedURL != null) {
            final String mergedABasePath = mergedURL.getBasePath();
            if (StringUtils.isNotEmpty(mergedABasePath)) {
                return mergedABasePath;
            }
        }
        return basePath;
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
                    final String hostStr = ForestVariable.getStringValue(this.host, request);
                    final String originHost = hostStr;
                    final String schemeStr = ForestVariable.getStringValue(this.scheme, request);
                    final URL url = new URL(this.basePath);
                    if (forced || StringUtils.isEmpty(schemeStr)) {
                        setScheme(url.getProtocol());
                    }
                    if (forced || StringUtils.isEmpty(this.userInfo)) {
                        this.userInfo = url.getUserInfo();
                    }
                    if (forced || StringUtils.isEmpty(hostStr)) {
                        this.host = ForestVariable.create(url.getHost());
                    }
                    final Integer portInt = url.getPort();
                    if (forced || (URLUtils.isNonePort(portInt) && StringUtils.isEmpty(originHost))) {
                        this.port = ForestVariable.create(url.getPort());
                    }
                    this.basePath = url.getPath();
                } catch (MalformedURLException e) {
                    throw new ForestRuntimeException(e);
                }
            } else {
                this.basePath = "/" + this.basePath;
            }
        }
        needUrlRegenerate();
        return this;
    }

    /**
     * 获取URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port + BasePath 后部分
     *
     * @return URL路径
     */
    public String getPath() {
        final String pathStr = ForestVariable.getStringValue(path, request);
        if (StringUtils.isNotEmpty(pathStr) && pathStr.charAt(0) != '/') {
            return '/' + pathStr;
        }
        return pathStr;
    }

    protected String getSchemeWithoutBaseURL() {
        return ForestVariable.getStringValue(scheme, request);
    }

    /**
     * 获取完整URL路径
     * <p>Full Path = baseURL path + basePath + path
     *
     * @return 完整URL路径
     * @since 1.7.4
     */
    public String getFullPath() {
        final StringBuilder builder = new StringBuilder();
        final String schemaStr = getSchemeWithoutBaseURL();
        if (StringUtils.isEmpty(schemaStr) && baseURL != null) {
            final String baseRet = baseURL.getPath();
            if (StringUtils.isNotEmpty(baseRet)) {
                builder.append(baseRet);
            }
        }
        final String basePathStr = getBasePath();
        if (StringUtils.isNotEmpty(basePathStr)) {
            if (host != null && basePathStr.charAt(0) != '/') {
                builder.append('/');
            }
            builder.append(basePathStr);
        }
        final String path = getPath();
        if (StringUtils.isNotEmpty(path)) {
            final int len =  builder.length() - 1;
            if (len >= 0) {
                if ((host != null || basePath != null) && path.charAt(0) != '/' && builder.charAt(len) != '/') {
                    builder.append('/');
                    builder.append(path);
                } else if (path.length() > 1 && path.charAt(0) == '/' && builder.charAt(len) == '/') {
                    builder.append(path.substring(1));
                } else {
                    builder.append(path);
                }
            } else {
                builder.append(path);
            }
        }
        return builder.toString();
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
        this.path = ForestVariable.create(path.trim());
        needUrlRegenerate();
        return this;
    }

    /**
     * 设置URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port + BasePath 后部分
     *
     * @param path URL路径变量
     * @return {@link ForestURL}对象实例
     * @since 1.7.4
     */
    public ForestURL setPath(ForestVariable path) {
        if (path == null) {
            return this;
        }
        this.path = path;
        needUrlRegenerate();
        return this;
    }


    public String getUserInfo() {
        if (StringUtils.isEmpty(userInfo) && address != null) {
            return address.getUserInfo();
        }
        if (baseURL != null) {
            final String baseUserInfo = baseURL.getUserInfo();
            if (StringUtils.isNotEmpty(userInfo)) {
                return baseUserInfo;
            }
        }
        return userInfo;
    }

    public ForestURL setUserInfo(String userInfo) {
        this.userInfo = userInfo;
        needUrlRegenerate();
        return this;
    }

    public String getAuthority() {
        final StringBuilder builder = new StringBuilder();
        final String userInfo = getUserInfo();
        final String host = getHost();
        final int port = getPort();
        final boolean ssl = isSSL();
        if (StringUtils.isNotEmpty(userInfo)) {
            builder.append(URLUtils.userInfoEncode(userInfo, "UTF-8")).append("@");
        }
        if (StringUtils.isNotEmpty(host)) {
            builder.append(URLUtils.userInfoEncode(host, "UTF-8"));
        }
        if (URLUtils.isNotNonePort(port) &&
                ((port != 80 && port != 443 && port > -1) ||
                (port == 80 && ssl) ||
                (port == 443 && !ssl))) {
            builder.append(':').append(port);
        }
        return builder.toString();
    }

    public String getRef() {
        String refStr = ForestVariable.getStringValue(ref, request);
        if (StringUtils.isNotEmpty(refStr)) {
            return refStr;
        }
        if (baseURL != null) {
            final String baseRef = baseURL.getRef();
            if (StringUtils.isNotEmpty(baseRef)) {
                return baseRef;
            }
        }
        return refStr;
    }

    public ForestURL setRef(String ref) {
        if (ref == null) {
            this.ref = null;
        } else {
            this.ref = ForestVariable.create(ref);
        }
        return this;
    }

    public boolean isSSL() {
        final String scheme = getScheme();
        return "https".equals(scheme);
    }

    public String toURLString() {
        final StringBuilder builder = new StringBuilder();
        final String scheme = getScheme();
        if (StringUtils.isNotEmpty(scheme)) {
            builder.append(scheme).append("://");
        }
        final String authority = getAuthority();
        if (StringUtils.isNotEmpty(authority)) {
            builder.append(authority);
        }
        final String host = getHost();
        if (StringUtils.isEmpty(host)) {
            builder.setLength(0);
        }

        final String fullPath = getFullPath();
        if (StringUtils.isNotEmpty(fullPath)) {
            final String encodedFullPath = URLUtils.pathEncode(fullPath, "UTF-8");
            if (host != null && encodedFullPath.charAt(0) != '/') {
                builder.append('/');
            }
            builder.append(encodedFullPath);
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
        final String ref = getRef();
        if (StringUtils.isNotEmpty(ref)) {
            return getOriginalUrl() + "#" + ref;
        }
        return getOriginalUrl();
    }



    public URL toJavaURL() {
        try {
            return new URL(getOriginalUrl());
        } catch (MalformedURLException e) {
            throw new ForestRuntimeException(e);
        }
    }

    public URI toURI() {
        try {
            return new URI(getOriginalUrl());
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
        if (forced && address != null) {
            if (StringUtils.isNotEmpty(address.getScheme())) {
                this.scheme = ForestVariable.create(address.getScheme());
            }
            if (StringUtils.isNotEmpty(address.getHost())) {
                this.host = ForestVariable.create(address.getHost());
            }
            if (URLUtils.isNotNonePort(address.getPort())) {
                this.port = ForestVariable.create(address.getPort());
            }
            if (StringUtils.isNotEmpty(address.getBasePath())) {
                this.basePath = address.getBasePath();
            }
            needUrlRegenerate();
        }
        this.address = address;
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
       
//        ForestVariable newSchema = this.scheme == null ? url.scheme : this.scheme;
//        String newUserInfo = this.userInfo == null ? url.userInfo : this.userInfo;
//        ForestVariable newHost = this.host == null ? url.host : this.host;
//        ForestVariable newPort = this.port == null ? url.port : this.port;
//        ForestVariable newPath = this.path == null ? url.path : this.path;
//        ForestVariable newRef = this.ref == null ? url.ref : this.ref;
//        return new ForestURL(newSchema, newUserInfo, newHost, newPort, newPath, newRef);
        url.mergedURL = null;
        final ForestURL newURL = new ForestURL(url, this.scheme, this.userInfo, this.host, this.port, this.path, this.ref);
        newURL.query = this.query;
        return newURL;
    }


    /**
     * 设置基地址URL
     *
     * @param baseURL 基地址URL
     * @return {@link ForestURL}对象实例
     */
    public ForestURL setBaseURL(ForestURL baseURL) {
        this.baseURL = baseURL;
        this.baseURL.setRequest(request);
        return this;
    }

    public ForestURL getBaseURL() {
        return baseURL;
    }

    /**
     * 设置字符串形式的基地址URL
     *
     * @param baseURL 基地址URL字符串
     * @return {@link ForestURL}对象实例
     */
    public ForestURL setBaseURL(String baseURL) {
        return setBaseURL(ForestURL.parse(request, baseURL));
    }


    /**
     * 设置基地址URL
     *
     * @param baseURL 基地址URL
     * @return {@link ForestURL}对象实例
     */
    public ForestURL setBaseURL_back(ForestURL baseURL) {
        String baseSchema = "http";
        String baseUserInfo = null;
        String baseHost = "localhost";
        int basePort = -1;
        String basePath = null;
        boolean portChange = false;
        if (baseURL != null) {
            if (baseURL.scheme != null) {
                baseSchema = ForestVariable.getStringValue(baseURL.scheme, request);
            }
            if (baseURL.userInfo != null) {
                baseUserInfo = baseURL.userInfo;
            }
            if (baseURL.host != null) {
                baseHost = ForestVariable.getStringValue(baseURL.host, request);
            }
            final Integer basePortInt = ForestVariable.getIntegerValue(baseURL.port, request);
            if (URLUtils.isNotNonePort(basePortInt)) {
                basePort = basePortInt;
            }
            if (baseURL.path != null) {
                basePath = ForestVariable.getStringValue(baseURL.path, request);
            }
        }
        boolean needBasePath = false;
        if (this.scheme == null) {
            portChange = true;
            setScheme(baseSchema);
            needBasePath = true;
        }
        if (this.userInfo == null) {
            this.userInfo = baseUserInfo;
        }
        if (this.host == null) {
            this.host = ForestVariable.create(baseHost);
            needBasePath = true;
        }
        final Integer portInt = ForestVariable.getIntegerValue(this.port, request);
        if (portChange && URLUtils.isNonePort(portInt)) {
            this.port = ForestVariable.create(basePort);
        }
        final String pathStr = ForestVariable.getStringValue(this.path, request);
        String newPathStr = pathStr;
        if (StringUtils.isNotBlank(pathStr)) {
            if (pathStr.charAt(0) != '/') {
                newPathStr = '/' + pathStr;
            }
        }
        if (needBasePath && StringUtils.isNotBlank(basePath)) {
            if (basePath.charAt(basePath.length() - 1) == '/') {
                basePath = basePath.substring(0, basePath.length() - 1);
            }
            if (StringUtils.isEmpty(newPathStr)) {
                newPathStr = basePath;
            } else {
                newPathStr = basePath + newPathStr;
            }
        }
        if (StringUtils.isNotEmpty(newPathStr)) {
            this.path = ForestVariable.create(newPathStr);
        }
        needUrlRegenerate();
        return this;
    }

    public ForestURL mergeAddress() {
//        if (address != null) {
//            final String hostStr = ForestVariable.getStringValue(host, request);
//            String originHost = hostStr;
//            final String schemeStr = ForestVariable.getStringValue(scheme, request);
//            if (StringUtils.isEmpty(schemeStr)) {
//                scheme = ForestVariable.create(address.getScheme());
//                refreshSSL();
//            }
//            if (StringUtils.isEmpty(hostStr)) {
//                host = ForestVariable.create(address.getHost());
//            }
//            final Integer portInt = ForestVariable.getIntegerValue(port, request);
//            if (URLUtils.isNonePort(portInt) && StringUtils.isEmpty(originHost)) {
//                port = ForestVariable.create(address.getPort());
//            }
//            if (StringUtils.isEmpty(userInfo)) {
//                userInfo = address.getUserInfo();
//            }
//            if (StringUtils.isEmpty(basePath)) {
//                setBasePath(address.getBasePath(), false);
//            }
//            needUrlRegenerate();
//        }
        return this;
    }

    public ForestURL checkAndComplete() {
        String oldUrl = getOriginalUrl();
        final String schemeStr = getScheme();
        if (StringUtils.isEmpty(schemeStr)) {
            setScheme(ssl ? "https" : "http");
        }
        final String hostStr = getHost();
        if (StringUtils.isEmpty(hostStr)) {
            setHost("localhost");
            final Integer portInt = ForestVariable.getIntegerValue(port, request);
            if (URLUtils.isNonePort(portInt)) {
                log.warn("[Forest] Invalid url '" + oldUrl + "'. But an valid url must start width 'http://' or 'https://'. Convert this url to '" + toURLString() + "' automatically!");
            } else {
                log.warn("[Forest] Invalid url '" + oldUrl + "'. Host is empty. Convert this url to '" + toURLString() + "' automatically!");
            }
        }
        return this;
    }
    
    protected void checkAndReparseUrl() {
        if (variablesChanged) {
            needUrlRegenerate();
        }
    }

    @Override
    public void clear() {
        if (baseURL != null) {
            baseURL.clear();
        }
        if (mergedURL != null) {
            mergedURL.clear();
        }
    }

    @Override
    public void onChanged(MappingTemplate template, Object newValue) {
    }

    public ForestURL clone(ForestRequest newRequest) {
        ForestURL clone = new ForestURL();
        clone.request = newRequest;
        clone.mergedURL = mergedURL == null ? null : this.mergedURL;
        clone.baseURL = baseURL == null ? null : baseURL.clone(newRequest);
        clone.address = address;
        clone.baseAddress = baseAddress;
        clone.query = query == null ? null : query.clone(newRequest);
        clone.scheme = scheme;
        clone.host = host;
        clone.port = port;
        clone.userInfo = userInfo;
        clone.basePath = basePath;
        return clone;
    }
}
