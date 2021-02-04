package com.dtflys.forest.http;

/**
 * Forest请求正向代理
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.5.0-BETA5
 */
public class ForestProxy {

    private final String host;

    private final int port;

    private ForestHeaderMap headers;

    public ForestProxy(String ip, int port) {
        this.host = ip;
        this.port = port;
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public ForestHeaderMap getHeaders() {
        return headers;
    }

    public void setHeaders(ForestHeaderMap headers) {
        this.headers = headers;
    }
}


