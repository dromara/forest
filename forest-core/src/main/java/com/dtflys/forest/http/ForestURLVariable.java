package com.dtflys.forest.http;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.MappingURLTemplate;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ForestURLVariable implements ForestURL {

    private final static Logger log = LoggerFactory.getLogger(SimpleForestURL.class);

    private final ForestRequest<?> request;

    private MappingURLTemplate urlTemplate;

    /**
     * 原始URL
     * <p>即为整个完整的没被拆分的URL字符串
     */
    String originalUrl;

    ForestAddress address;

    /**
     * HTTP协议
     */
    String scheme;

    /**
     * 主机地址
     */
    String host;

    /**
     * 主机端口
     */
    Integer port;

    /**
     * URL根路径
     */
    String basePath;

    /**
     * URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     */
    String path;

    /**
     * 用户信息
     *
     * <p>包含在URL中的用户信息，比如:
     * <p>URL http://xxx:yyy@localhost:8080 中 xxx:yyy 的部分为用户信息
     * <p>其中，xxx为用户名，yyy为用户密码
     */
    String userInfo;

    /**
     * reference
     * <p>URL井号(#)后面的字符串
     */
    String ref;

    /**
     * 是否为SSL
     */
    boolean ssl;

    /**
     * 是否需要重新生成 URL
     */
    volatile boolean needRegenerateUrl = false;


    public ForestURLVariable(ForestRequest<?> request) {
        this.request = request;
    }

    public ForestURLVariable(
            ForestRequest<?> request,
            String scheme,
            String userInfo,
            String host,
            Integer port,
            String path,
            String ref) {
        this.request = request;
        setScheme(scheme);
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = path;
        this.ref = ref;
        needRegenerateUrl();
    }


    public MappingURLTemplate getUrlTemplate() {
        return urlTemplate;
    }

    public void setUrlTemplate(MappingURLTemplate urlTemplate) {
        this.urlTemplate = urlTemplate;
        render(true);
    }

    public void render(boolean allowUndefinedVariable) {
        if (urlTemplate == null) {
            return;
        }
        SimpleForestURL newUrl = urlTemplate.renderURL(request, allowUndefinedVariable);
        if (newUrl.address != null) {
            this.address = newUrl.address;
        }
        if (newUrl.scheme != null) {
            setScheme(newUrl.scheme);
        }
        if (newUrl.userInfo != null) {
            this.userInfo = newUrl.userInfo;
        }
        if (newUrl.host != null) {
            this.host = newUrl.host;
        }
        if (newUrl.port != null) {
            this.port = newUrl.port;
        }
        if (newUrl.basePath != null) {
            this.basePath = newUrl.basePath;
        }
        if (newUrl.path != null) {
            this.path = newUrl.path;
        }
        if (newUrl.ref != null) {
            this.ref = newUrl.ref;
        }
        if (newUrl.ssl != null) {
            this.ssl = newUrl.ssl;
        }
        needRegenerateUrl();
    }

    @Override
    public void needRegenerateUrl() {
        needRegenerateUrl = true;
    }

    /**
     * 获取原始URL
     *
     * @return 原始URL字符串
     */
    @Override
    public String getOriginalUrl() {
        if (originalUrl == null || needRegenerateUrl) {
            render(true);
            originalUrl = toURLString();
            needRegenerateUrl = false;
        }
        return originalUrl;
    }

    /**
     * 设置基础地址信息
     *
     * @param baseAddress {@link ForestAddress}对象
     * @return {@link ForestURL}对象
     */
    @Override
    public ForestURL setBaseAddress(ForestAddress baseAddress) {
        if (baseAddress != null) {
            final String baseScheme = baseAddress.getScheme();
            final String baseHost = baseAddress.getHost();
            final String bastPath = baseAddress.getBasePath();

            final int basePort = baseAddress.getPort();
            setBasePath(bastPath);
            setScheme(baseScheme);
            setHost(baseHost);
            if (basePort != -1) {
                setPort(basePort);
            }
        }
        return this;
    }

    @Override
    public String getScheme() {
        if (StringUtils.isEmpty(scheme) && address != null) {
            return address.getScheme();
        }
        if (StringUtils.isEmpty(scheme)) {
            return ssl ? "https" : "http";
        }
        return scheme;
    }

    @Override
    public ForestURL setScheme(String scheme) {
        if (StringUtils.isBlank(scheme)) {
            return this;
        }
        this.scheme = scheme.trim();
        refreshSSL();
        needRegenerateUrl();
        return this;
    }

    @Override
    public void refreshSSL() {
        this.ssl = "https".equals(this.scheme);
    }

    @Override
    public String getHost() {
        if (StringUtils.isEmpty(host) && address != null) {
            return address.getHost();
        }
        return host;
    }

    @Override
    public ForestURL setHost(String host) {
        if (StringUtils.isBlank(host)) {
            return this;
        }
        this.host = host.trim();
        if (this.host.endsWith("/")) {
            this.host = this.host.substring(0, this.host.lastIndexOf("/"));
        }
        needRegenerateUrl();
        return this;
    }

    @Override
    public int getPort() {
        if (URLUtils.isNonePort(port) && address != null) {
            return ForestURL.normalizePort(address.getPort(), ssl);
        }
        return ForestURL.normalizePort(port, ssl);
    }

    @Override
    public ForestURL setPort(int port) {
        this.port = port;
        needRegenerateUrl();
        return this;
    }

    /**
     * 获取URL根路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @return URL根路径
     */
    @Override
    public String normalizeBasePath() {
        if (StringUtils.isEmpty(basePath)) {
            return normalizeBasePath(address.getBasePath());
        }
        return normalizeBasePath(basePath);
    }


    @Override
    public String normalizeBasePath(String basePath) {
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
    @Override
    public ForestURL setBasePath(String basePath) {
        return setBasePath(basePath, true);
    }

    /**
     * 设置URL根路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @param basePath 根路径
     * @param forced   是否强制修改, {@code true}: 强制修改非根路径部分地址信息, {@code false}: 非强制，如果URL已设置host、port等非根路径部分地址信息则不会修改
     * @return {@link ForestURL}对象实例
     */
    @Override
    public ForestURL setBasePath(String basePath, boolean forced) {
        if (basePath == null) {
            return this;
        }
        this.basePath = basePath.trim();
        if (!this.basePath.startsWith("/")) {
            if (URLUtils.isURL(this.basePath)) {
                try {
                    final String originHost = this.host;
                    final URL url = new URL(this.basePath);
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
        needRegenerateUrl();
        return this;
    }

    /**
     * 获取URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port + BasePath 后部分
     *
     * @return URL路径
     */
    @Override
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
    @Override
    public ForestURL setPath(String path) {
        if (path == null) {
            return this;
        }
        this.path = path.trim();
        needRegenerateUrl();
        return this;
    }

    @Override
    public String getUserInfo() {
        if (StringUtils.isEmpty(userInfo) && address != null) {
            return address.getUserInfo();
        }
        return userInfo;
    }

    @Override
    public ForestURL setUserInfo(String userInfo) {
        this.userInfo = userInfo;
        needRegenerateUrl();
        return this;
    }

    @Override
    public String getAuthority() {
        final StringBuilder builder = new StringBuilder();
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

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public ForestURL setRef(String ref) {
        this.ref = ref;
        return this;
    }

    @Override
    public boolean isSSL() {
        if (StringUtils.isEmpty(scheme) && address != null) {
            return "https".equals(address.getScheme());
        }
        return ssl;
    }

    @Override
    public String toURLString() {
        final StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotEmpty(scheme)) {
            builder.append(scheme).append("://");
        }
        final String authority = getAuthority();
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
            final int len = builder.length() - 1;
            if ((host != null || basePath != null) && encodedPath.charAt(0) != '/' && builder.charAt(len) != '/') {
                builder.append('/');
                builder.append(encodedPath);
            } else if (encodedPath.length() > 1 && encodedPath.charAt(0) == '/' && builder.charAt(len) == '/') {
                builder.append(encodedPath.substring(1));
            } else {
                builder.append(encodedPath);
            }

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
    @Override
    public ForestRoute getRoute() {
        return ForestRoutes.getRoute(getHost(), getPort());
    }

    @Override
    public String toString() {
        if (StringUtils.isNotEmpty(ref)) {
            return getOriginalUrl() + "#" + ref;
        }
        return getOriginalUrl();
    }


    @Override
    public URL toJavaURL() {
        try {
            return new URL(getOriginalUrl());
        } catch (MalformedURLException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
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
    @Override
    public ForestURL setAddress(ForestAddress address) {
        return setAddress(address, true);
    }

    /**
     * 修改地址信息
     *
     * @param address 地址, {@link ForestAddress}对象实例
     * @param forced  是否强制修改, {@code true}: 强制修改, {@code false}: 非强制，如果URL已设置host、port等信息则不会修改
     * @return {@link ForestURL}对象实例
     */
    @Override
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
     *
     * @param url 被合并的一个URL
     * @return 合并完的新URL
     */
    @Override
    public ForestURLVariable mergeURLWith(SimpleForestURL url) {
        if (url == null) {
            return this;
        }
        String newSchema = this.scheme == null ? url.scheme : this.scheme;
        String newUserInfo = this.userInfo == null ? url.userInfo : this.userInfo;
        String newHost = this.host == null ? url.host : this.host;
        Integer newPort = this.port == null ? url.port : this.port;
        String newPath = this.path == null ? url.path : this.path;
        String newRef = this.ref == null ? url.ref : this.ref;

        return new ForestURLVariable(request, newSchema, newUserInfo, newHost, newPort, newPath, newRef);
    }

    public ForestURLVariable copyFromSimpleUrl(SimpleForestURL url) {
        if (url == null) {
            return this;
        }
        this.address = url.address;
        this.scheme = url.scheme;
        this.userInfo = url.userInfo;
        this.host = url.host;
        this.port = url.port;
        this.basePath = url.basePath;
        this.path = url.path;
        this.ref = url.ref;
        this.originalUrl = url.originalUrl;
        return this;
    }



    /**
     * 设置基地址URL
     *
     * @param baseURL 基地址URL
     * @return {@link ForestURL}对象实例
     */
    @Override
    public ForestURL setBaseURL(SimpleForestURL baseURL) {
        String baseSchema = "http";
        String baseUserInfo = null;
        String baseHost = "localhost";
        int basePort = -1;
        String basePath = null;
        boolean portChange = false;
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
            portChange = true;
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

        if (portChange && URLUtils.isNonePort(this.port)) {
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
        needRegenerateUrl();
        return this;
    }

    @Override
    public ForestURL mergeAddress() {
        if (address != null) {
            String originHost = host;
            if (StringUtils.isEmpty(scheme)) {
                scheme = address.getScheme();
                refreshSSL();
            }
            if (StringUtils.isEmpty(host)) {
                host = address.getHost();
            }
            if (URLUtils.isNonePort(port) && StringUtils.isEmpty(originHost)) {
                port = address.getPort();
            }
            if (StringUtils.isEmpty(userInfo)) {
                userInfo = address.getUserInfo();
            }
            if (StringUtils.isEmpty(basePath)) {
                setBasePath(address.getBasePath(), false);
            }
            needRegenerateUrl();
        }
        return this;
    }

    @Override
    public ForestURL checkAndComplete() {
        String oldUrl = getOriginalUrl();
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
