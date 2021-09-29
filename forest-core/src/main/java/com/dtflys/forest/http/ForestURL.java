package com.dtflys.forest.http;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;

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
    private int port;

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
            int basePort = baseAddress.getPort();
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
        if (port == -1) {
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
     * 获取URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @return URL路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
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
            builder.append(userInfo).append("@");
        }
        builder.append(host);
        if ((port != 80 && port != 443 && port > -1) ||
                (port == 80 && !ssl) ||
                (port == 443 && !ssl)) {
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
        StringBuilder builder = new StringBuilder(scheme).append("://");
        builder.append(getAuthority()).append(path);
        if (StringUtils.isNotEmpty(ref)) {
            builder.append("#").append(ref);
        }
        return builder.toString();

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
}
