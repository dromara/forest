package com.dtflys.forest.http;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;

/**
 * Forest Cookie
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-RC1
 */
public class ForestCookie implements Serializable {

    /**
     * Cookie名称
     */
    private final String name;

    /**
     * Cookie内容
     */
    private final String value;

    /**
     * 创建时间
     */
    private final Date createTime;

    /**
     * 最大时长
     */
    private final Duration maxAge;

    /**
     * 域名
     */
    private final String domain;

    /**
     * 路径
     */
    private final String path;

    /**
     * 是否仅限HTTPS
     */
    private final boolean secure;

    /**
     * 是否仅限HTTP方式读取
     */
    private final boolean httpOnly;

    /**
     * 是否仅限主机名匹配
     */
    private final boolean hostOnly;

    public ForestCookie(String name, String value, Date createTime, Duration maxAge, String domain, String path, boolean secure, boolean httpOnly, boolean hostOnly) {
        this.name = name;
        this.value = value;
        this.createTime = createTime;
        this.maxAge = maxAge;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.hostOnly = hostOnly;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Duration getMaxAge() {
        return maxAge;
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public boolean isHostOnly() {
        return hostOnly;
    }

}
