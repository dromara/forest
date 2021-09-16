package com.dtflys.forest.http;

/**
 * Forest主机地址信息
 * <p>该地址信息为主机名/ip地址 + 端口号的组合
 */
public class ForestHostAddress {

    /**
     * 主机地址(主机名/ip地址)
     */
    private final String host;

    /**
     * 主机端口号
     */
    private final int port;

    public ForestHostAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 获取主机地址
     * <p>可以为主机名或IP地址
     *
     * @return 主机地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 获取主机地址(主机名/ip地址)
     *
     * @return 主机端口号
     */
    public int getPort() {
        return port;
    }
}
