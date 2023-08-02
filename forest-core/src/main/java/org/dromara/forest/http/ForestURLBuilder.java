package org.dromara.forest.http;

/**
 * Forest URL 构造器
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.22
 */
public class ForestURLBuilder {

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

    public String getScheme() {
        return scheme;
    }

    public ForestURLBuilder setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ForestURLBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public ForestURLBuilder setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getBasePath() {
        return basePath;
    }

    public ForestURLBuilder setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public String getPath() {
        return path;
    }

    public ForestURLBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public ForestURLBuilder setUserInfo(String userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public String getRef() {
        return ref;
    }

    public ForestURLBuilder setRef(String ref) {
        this.ref = ref;
        return this;
    }

    public boolean isSsl() {
        return ssl;
    }

    public ForestURLBuilder setSsl(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    /**
     * 进行构造，产生Forest URL对象
     *
     * @return {@link ForestURL}对象实例
     */
    public ForestURL build() {
        return new ForestURL(scheme, userInfo, host, port, path, ref).setBasePath(basePath);
    }
}
