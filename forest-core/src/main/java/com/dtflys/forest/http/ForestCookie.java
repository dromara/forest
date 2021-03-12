package com.dtflys.forest.http;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.internal.http.HttpDate;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;

import static okhttp3.internal.Util.delimiterOffset;
import static okhttp3.internal.Util.verifyAsIpAddress;

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
    private String value;

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

    private final boolean persistent;

    public ForestCookie(String name, String value, Date createTime, Duration maxAge, String domain, String path, boolean secure, boolean httpOnly, boolean hostOnly, boolean persistent) {
        this.name = name;
        this.value = value;
        this.createTime = createTime;
        this.maxAge = maxAge;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.hostOnly = hostOnly;
        this.persistent = persistent;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Date getCreateTime() {
        return createTime;
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

    public static boolean matchDomain(String leftDomain, String rightDomain) {
        if (leftDomain.equals(rightDomain)) {
            return true;
        }
        if (leftDomain.endsWith(rightDomain)
                && leftDomain.charAt(leftDomain.length() - rightDomain.length() - 1) == '.'
                && !verifyAsIpAddress(leftDomain)) {
            return true;
        }
        return false;
    }

    /**
     * 是否匹配域名
     *
     * @param domain 域名
     * @return  {@code true}: 匹配, {@code false}: 不匹配
     */
    public boolean matchDomain(String domain) {
        return matchDomain(this.domain, domain);
    }

    public static boolean matchPath(String leftPath, String rightPath) {
        if (leftPath.equals(rightPath)) {
            return true;
        }
        if (leftPath.startsWith(rightPath)) {
            if (rightPath.endsWith("/")) {
                return true;
            }
            return leftPath.charAt(rightPath.length()) == '/';
        }
        return false;

    }

    public boolean matchPath(String path) {
        return matchPath(this.path, path);
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 判断Cookie是否过期
     *
     * @param date 当前日期
     * @return {@code true}: 已过期, {@code false}: 未过期
     */
    public boolean isExpired(Date date) {
        long expiredTime = getExpiresTime();
        return expiredTime <= date.getTime();
    }

    public static ForestCookie createFromHttpclientCookie(org.apache.http.cookie.Cookie httpCookie) {
        long currentTime = System.currentTimeMillis();
        Date expiresDate = httpCookie.getExpiryDate();
        long maxAge;
        if (expiresDate != null) {
            long expiresAt = expiresDate.getTime();
            if (expiresAt > currentTime) {
                maxAge = expiresAt - currentTime;
            } else {
                maxAge = 0L;
            }
        } else {
            maxAge = Long.MAX_VALUE;
        }
        Date createTime = new Date(currentTime);
        Duration maxAgeDuration = Duration.ofMillis(maxAge);
        return new ForestCookie(
                httpCookie.getName(),
                httpCookie.getValue(),
                createTime,
                maxAgeDuration,
                httpCookie.getDomain(),
                httpCookie.getPath(),
                httpCookie.isSecure(),
                false,
                true,
                false);
    }


    public static ForestCookie createFromOkHttpCookie(Cookie okCookie) {
        long currentTime = System.currentTimeMillis();
        long expiresAt = okCookie.expiresAt();
        long maxAge;
        if (expiresAt > currentTime) {
            maxAge = expiresAt - currentTime;
        } else {
            maxAge = 0L;
        }
        Date createTime = new Date(currentTime);
        Duration maxAgeDuration = Duration.ofMillis(maxAge);
        return new ForestCookie(
                okCookie.name(),
                okCookie.value(),
                createTime,
                maxAgeDuration,
                okCookie.domain(),
                okCookie.path(),
                okCookie.secure(),
                okCookie.httpOnly(),
                okCookie.hostOnly(),
                okCookie.persistent());
    }


    public static ForestCookie parse(String url, String setCookie) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        Cookie okCookie = Cookie.parse(httpUrl, setCookie);
        return createFromOkHttpCookie(okCookie);
    }

    public long getExpiresTime() {
        long maxAgeMilis = maxAge.toMillis();
        if (maxAgeMilis == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        long expiresTime = createTime.getTime() + maxAge.toMillis();
        return expiresTime;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(name);
        result.append('=');
        result.append(value);

        if (persistent) {
            if (maxAge.toMillis() == 0L) {
                result.append("; max-age=0");
            } else {
                long expiresTime = getExpiresTime();
                result.append("; expires=").append(HttpDate.format(new Date(expiresTime)));
            }
        }

        if (!hostOnly) {
            result.append("; domain=");
            result.append(domain);
        }

        result.append("; path=").append(path);

        if (secure) {
            result.append("; secure");
        }

        if (httpOnly) {
            result.append("; httponly");
        }

        return result.toString();
    }

}
