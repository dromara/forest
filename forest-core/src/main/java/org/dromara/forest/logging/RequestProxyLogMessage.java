package org.dromara.forest.logging;

/**
 * 请求正向代理日志消息
 * <p>封装了请求正向代理日志打印所需的所有信息</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.5.0-BETA5
 */
public class RequestProxyLogMessage {

    /**
     * 代理类型
     */
    private String type;

    /**
     * 代理主机地址
     */
    private String host;

    /**
     * 代理服务端口
     */
    private String port;

    private String[] headers;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }
}
