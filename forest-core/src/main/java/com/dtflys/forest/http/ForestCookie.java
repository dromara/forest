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

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLUtils;

import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


/**
 * Forest Cookie
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-RC1
 */
public class ForestCookie implements Cloneable, Serializable {

    /**
     * Cookie名称
     */
    private String name;

    /**
     * Cookie内容
     */
    private String value;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最大时长
     */
    private Duration maxAge;

    /**
     * 域名
     */
    private String domain;

    /**
     * 路径
     */
    private String path;

    /**
     * 版本
     */
    private int version = 0;

    /**
     * 是否仅限HTTPS
     */
    private boolean secure;

    /**
     * 是否仅限HTTP方式读取
     */
    private boolean httpOnly;

    /**
     * 是否仅限主机名匹配
     */
    private boolean hostOnly;

    /**
     * 是否持久化
     */
    private boolean persistent;

    /**
     * Forest Cookie 构造函数
     *
     * @param name Cookie名
     * @param value Cookie值
     * @author gongjun[dt_flys@hotmail.com]
     * @since 1.5.23
     */
    public ForestCookie(String name, String value) {
        this(name, value, new Date(), null, null, "/", false, false, false, false);
    }

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

    public static ForestCookie nameValue(String name, String value) {
        return new ForestCookie(name, value);
    }

    public static ForestCookie name(String name) {
        return new ForestCookie(name, "");
    }


    public String getName() {
        return name;
    }

    public ForestCookie setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ForestCookie setValue(String value) {
        this.value = value;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public ForestCookie setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Duration getMaxAge() {
        return maxAge;
    }

    public int getMaxAgeAsSeconds() {
        if (maxAge == null) {
            return -1;
        }
        return (int) maxAge.getSeconds();
    }


    public ForestCookie setMaxAge(Duration maxAge) {
        this.maxAge = maxAge;
        return this;
    }


    public ForestCookie setMaxAge(int maxAge) {
        this.maxAge = Duration.ofMillis(maxAge);
        return this;
    }


    /**
     * 获取Cookie所在的域名
     *
     * @return 域名
     */
    public String getDomain() {
        return domain;
    }

    /**
     * 设置Cookie所在的域名
     *
     * @param domain 域名
     * @return {@link ForestCookies}类实例
     */
    public ForestCookie setDomain(String domain) {
        if (domain == null) {
            throw new NullPointerException("[Forest] cookie domain is null");
        }
        this.domain = domain;
        return this;
    }

    /**
     * 获取Cookie所在的URL路径
     *
     * @return URL路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置Cookie所在的URL路径
     *
     * @param path URL路径
     * @return {@link ForestCookies}类实例
     */
    public ForestCookie setPath(String path) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("[Forest] cookie path must start with '/'");
        }
        this.path = path;
        return this;
    }

    /**
     * 获取Cookie版本
     *
     * @return 版本
     * @since 1.7.0
     */
    public int getVersion() {
        return version;
    }

    /**
     * 设置Cookie版本
     *
     * @param version 版本
     * @return {@link ForestCookies}类实例
     */
    public ForestCookie setVersion(int version) {
        this.version = version;
        return this;
    }

    /**
     * Cookie是否安全
     * 如果该属性为{@code true}, 只能⽤ HTTPS 协议发送给服务器
     *
     * @return {@code true}: 安全, {@code false}: 非安全
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * 设置Cookie是否安全
     * 如果该属性为{@code true}, 只能⽤ HTTPS 协议发送给服务器
     *
     * @param secure {@code true}: 安全, {@code false}: 非安全
     * @return {@link ForestCookies}类实例
     */
    public ForestCookie setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    /**
     * Cookie是否能被js获取到
     * 如果该属性为{@code true}, 则 Cookie 不能被js获取到
     *
     * @return {@code true}: 不能被js获取到, {@code false}: 能被js获取到
     */
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * Cookie是否能被js获取到
     * 如果该属性为{@code true}, 则 Cookie 不能被js获取到
     *
     * @param httpOnly {@code true}: 不能被js获取到, {@code false}: 能被js获取到
     * @return {@link ForestCookies}类实例
     */
    public ForestCookie setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    /**
     * 是否为 HostOnly Cookie
     *
     * @return {@code true}: 是 HostOnly, {@code false}: 不是 HostOnly
     */
    public boolean isHostOnly() {
        return hostOnly;
    }

    /**
     * 设置是否为 HostOnly Cookie
     *
     * @param hostOnly {@code true}: 是 HostOnly, {@code false}: 不是 HostOnly
     * @return {@link ForestCookies}类实例
     */
    public ForestCookie setHostOnly(boolean hostOnly) {
        this.hostOnly = hostOnly;
        return this;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public ForestCookie setPersistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    public static boolean matchDomain(boolean hostOnly, String leftDomain, String rightDomain) {
        if (leftDomain == null) {
            return true;
        }
        if (leftDomain.equals(rightDomain)) {
            return true;
        }
        if (!hostOnly && leftDomain.endsWith(rightDomain)
                && leftDomain.charAt(leftDomain.length() - rightDomain.length() - 1) == '.'
                && !URLUtils.isValidIPAddress(leftDomain)) {
            return true;
        }
        return URLUtils.matchDomain(leftDomain, rightDomain);
    }

    /**
     * 是否匹配域名
     *
     * @param domain 域名
     * @return  {@code true}: 匹配, {@code false}: 不匹配
     */
    public boolean matchDomain(String domain) {
        return URLUtils.matchDomain(domain, this.domain);
        // return matchDomain(this.hostOnly, this.domain, domain);
    }


    public static boolean matchPath(String cookiePath, String urlPath) {
        if (urlPath.equals(cookiePath)) {
            return true;
        }
        if (urlPath.startsWith(cookiePath)) {
            if (cookiePath.endsWith("/")) {
                return true;
            }
            return urlPath.charAt(cookiePath.length()) == '/';
        }
        return false;
    }

    /**
     * 匹配URL
     *
     * @param url {@link ForestURL}对象实例
     * @return  {@code true}: 匹配, {@code false}: 不匹配
     */
    public boolean matchURL(ForestURL url) {
        if (!matchSchema(url.getScheme())) return false;
        if (!matchDomain(url.getHost())) return false;
        if (!matchPath(url.getPath())) return false;
        return true;
    }

    /**
     * 匹配 HTTP 协议
     *
     * @param schema HTTP 协议
     * @return {@code true}: 匹配, {@code false}: 不匹配
     * @since 1.5.25
     */
    public boolean matchSchema(String schema) {
        if (!secure) {
            return true;
        }
        return "https".equals(schema);
    }

    /**
     * 匹配url路径
     *
     * @param path url路径
     * @return {@code true}: 匹配, 否则：不匹配
     */
    public boolean matchPath(String path) {
        return matchPath(this.path, path);
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

    public static ForestCookie parse(final String url, final String setCookie) {
        return parse(ForestURL.fromUrl(url), System.currentTimeMillis(), setCookie);
    }

    public static ForestCookie parse(final String url, long currentTimeMillis, final String setCookie) {
        return parse(ForestURL.fromUrl(url), currentTimeMillis, setCookie);
    }

    public static ForestCookie parse(ForestURL url, long currentTimeMillis, String setCookieString) {
        final String source = StringUtils.isBlank(setCookieString) ? "" : setCookieString.trim();
        final String[] nameValuePairs = source.split(";");

        String cookieName = null;
        String cookieValue = null;
        String domain = null;
        String path = null;
        long expiresAt = -1;
        Duration maxAge = null;
        int version = 0;
        boolean secure = false;
        boolean httpOnly = false;
        boolean hostOnly = true;
        boolean persistent = false;

        for (int i = 0; i < nameValuePairs.length; i++) {
            final String pair = nameValuePairs[i].trim();
            final String[] nameValue = pair.split("=", 2);
            final String name = nameValue[0].trim();
            final String value = nameValue.length > 1 ? nameValue[1].trim() : "";
            if (i == 0) {
                cookieName = name;
                cookieValue = value;
            } else if ("Max-Age".equalsIgnoreCase(name)) {
                final long maxAgeValue = parseMaxAge(value);
                maxAge = maxAgeValue < 0 ? null : Duration.ofSeconds(maxAgeValue);
                persistent = true;
            } else if ("Expires".equalsIgnoreCase(name)) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat(DatePattern.HTTP_DATETIME_PATTERN, Locale.ENGLISH);
                    Date date = formatter.parse(value);
                    expiresAt = date.getTime();
                    persistent = true;
                } catch (Throwable th) {
                    throw new ForestRuntimeException(th);
                }
            } else if ("Domain".equalsIgnoreCase(name)) {
                domain = parseDomain(value);
                hostOnly = false;
            } else if ("Path".equalsIgnoreCase(name)) {
                path = value;
            } else if ("Secure".equalsIgnoreCase(name)) {
                secure = true;
            } else if ("Version".equalsIgnoreCase(name)) {
                version = Integer.parseInt(value);
            } else if ("HttpOnly".equalsIgnoreCase(name)) {
                httpOnly = true;
            } else if ("HostOnly".equalsIgnoreCase(name)) {
                hostOnly = true;
            }
        }
        String urlHost = url.getHost();
        if (domain == null) {
            domain = urlHost;
        }
        if (!URLUtils.matchDomain(urlHost, domain)) {
            return null;
        }
        
        if (path == null) {
            path = "/";
        }

        if (maxAge == null && expiresAt != -1) {
            final long maxAgeValue = expiresAt - currentTimeMillis;
            if (maxAgeValue >= 0) {
                maxAge = Duration.ofSeconds(maxAgeValue);
            }
        }

        final ForestCookie cookie = new ForestCookie(
                cookieName,
                cookieValue,
                new Date(),
                maxAge,
                domain,
                path,
                secure,
                httpOnly,
                hostOnly,
                persistent);
        cookie.setVersion(version);
        return cookie;
    }

    private static String parseDomain(String s) {
        if (s.endsWith(".")) {
            throw new IllegalArgumentException();
        } else {
            if (s.startsWith(".")) {
                s = s.substring(1);
            }

            String canonicalDomain = URLUtils.getValidHost(s);
            if (canonicalDomain == null) {
                throw new IllegalArgumentException();
            } else {
                return canonicalDomain;
            }
        }
    }


    private static long parseMaxAge(String s) {
        try {
            long num = Long.parseLong(s);
            return num <= 0L ? -1 : num;
        } catch (NumberFormatException e) {
            if (s.matches("-?\\d+")) {
                return s.startsWith("-") ? -1 : Long.MAX_VALUE;
            }
            throw e;
        }
    }


    public long getExpiresTime() {
        return createTime.getTime() + maxAge.toMillis();
    }

    /**
     * 转换成 Java Cookie 对象
     *
     * @return Java Cookie 对象
     * @since 1.7.0
     */
    public javax.servlet.http.Cookie toJavaCookie() {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(getMaxAgeAsSeconds());
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setSecure(secure);
        cookie.setHttpOnly(httpOnly);
        cookie.setVersion(version);
        return cookie;
    }

    /**
     * 克隆Cookie对象
     *
     * @return {@link ForestCookie}对象实例
     * @since 1.5.23
     */
    @Override
    public ForestCookie clone() {
        return new ForestCookie(
                name,
                value,
                createTime,
                maxAge,
                domain,
                path,
                secure,
                httpOnly,
                hostOnly,
                persistent
        ).setVersion(version);
    }

    /**
     * 获取 Set-Cookie 头的字符串
     *
     * @return Set-Cookie 头字符串
     * @since 1.7.0
     */
    public String toSetCookieString() {
        final StringBuilder result = new StringBuilder();
        result.append(name);
        result.append('=');
        result.append(value);

        if (persistent) {
            final int maxAgeSeconds = getMaxAgeAsSeconds();
            result.append("; Max-Age=").append(maxAgeSeconds);
        }

        if (!hostOnly) {
            result.append("; Domain=");
            result.append(domain);
        }

        result.append("; Path=").append(path);

        if (secure) {
            result.append("; Secure");
        }

        if (httpOnly) {
            result.append("; HttpOnly");
        }

        return result.toString();
    }

    /**
     * 获取 Cookie 头的字符串
     *
     * @return Cookie 头字符串
     * @since 1.7.0
     */
    public String toCookieString() {
        return name + "=" + value;
    }


    @Override
    public String toString() {
        return toSetCookieString();
    }

}
