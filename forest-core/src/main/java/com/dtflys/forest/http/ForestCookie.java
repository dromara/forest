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
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.HashUtil;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLUtils;

import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Locale;


/**
 * Forest Cookie
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-RC1
 */
public class ForestCookie implements Cloneable, Serializable {

    private final static SimpleDateFormat EXPIRES_DATE_FORMAT =
            new SimpleDateFormat(DatePattern.HTTP_DATETIME_PATTERN, Locale.ENGLISH);

    private final static SimpleDateFormat EXPIRES_DATE_FORMAT2 =
            new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.ENGLISH);

    private final static SimpleDateFormat EXPIRES_DATE_FORMAT3 =
            new SimpleDateFormat("EEE, dd/MMM/yyyy HH:mm:ss z", Locale.ENGLISH);

    private final static SimpleDateFormat EXPIRES_DATE_FORMAT4 =
            new SimpleDateFormat("EEE, dd\\MMM\\yyyy HH:mm:ss z", Locale.ENGLISH);




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
     * 注释
     */
    private String comment;

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

    public ForestCookie(String name,
                        String value,
                        Date createTime,
                        Duration maxAge,
                        String domain,
                        String path,
                        boolean secure,
                        boolean httpOnly,
                        boolean hostOnly,
                        boolean persistent) {
        this(name, value, createTime, maxAge, null, domain, path, 0, secure, httpOnly, hostOnly, persistent);
    }


    public ForestCookie(String name,
                        String value,
                        Date createTime,
                        Duration maxAge,
                        String comment,
                        String domain,
                        String path,
                        int version,
                        boolean secure,
                        boolean httpOnly,
                        boolean hostOnly,
                        boolean persistent) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Cookie name cannot be empty");
        }

        if (!isValidCookieName(name) ||
                "Max-Age".equalsIgnoreCase(name) ||
                "Expires".equalsIgnoreCase(name) ||
                "Domain".equalsIgnoreCase(name) ||
                "Path".equalsIgnoreCase(name) ||
                "Secure".equalsIgnoreCase(name) ||
                "HttpOnly".equalsIgnoreCase(name) ||
                "HostOnly".equalsIgnoreCase(name) ||
                "Version".equalsIgnoreCase(name) ||
                name.startsWith("$")) {
            throw new IllegalArgumentException("Cookie name is invalid");
        }

        this.name = name;
        this.value = value;
        this.createTime = createTime;
        this.maxAge = maxAge;
        this.comment = comment;
        this.domain = domain;
        this.path = path;
        this.version = version;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.hostOnly = hostOnly;
        this.persistent = persistent;
    }

    private static boolean isValidCookieName(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (c < 0x20 || c >= 0x7f) {
                return false;
            }
        }
        return true;
    }

    /**
     * 通过Cookie名和Cookie值创建一个Cookie
     *
     * @param name Cookie名
     * @param value Cookie值
     * @return Forest Cookie 对象
     * @since 1.7.0
     */
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
     * 获取Cookie的注释
     *
     * @return 注释
     */
    public String getComment() {
        return comment;
    }

    /**
     * 设置Cookie的注释
     *
     * @param comment 注释
     * @return {@link ForestCookies}类实例
     * @since 1.7.0
     */
    public ForestCookie setComment(String comment) {
        this.comment = comment;
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
                && URLUtils.isValidIPAddress(leftDomain)) {
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
        if (domain != null && !matchDomain(url.getHost())) return false;
        if (path != null && !matchPath(url.getPath())) return false;
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
        if (maxAge == null) {
            return false;
        }
        long expiredTime = getExpiresTime();
        return expiredTime <= date.getTime();
    }

    /**
     * 判断Cookie是否过期
     *
     * @param currentTimeMillis 当前时间戳
     * @return {@code true}: 已过期, {@code false}: 未过期
     */
    public boolean isExpired(long currentTimeMillis) {
        if (maxAge == null) {
            return false;
        }
        long expiredTime = getExpiresTime();
        return expiredTime <= currentTimeMillis;
    }


    public static ForestCookie parse(final String setCookie) {
        return parse((ForestURL) null, System.currentTimeMillis(), setCookie);
    }


    public static ForestCookie parse(final String url, final String setCookie) {
        return parse(ForestURL.fromUrl(url), System.currentTimeMillis(), setCookie);
    }

    public static ForestCookie parse(final String url, long currentTimeMillis, final String setCookie) {
        return parse(ForestURL.fromUrl(url), currentTimeMillis, setCookie);
    }

    public static ForestCookie parse(ForestURL url, long currentTimeMillis, String setCookieString) {
        if (StringUtils.isBlank(setCookieString)) {
            return null;
        }

        final String source = StringUtils.isBlank(setCookieString) ? "" : setCookieString.trim();
        final String[] nameValuePairs = source.split(";");

        String cookieName = null;
        String cookieValue = null;
        String comment = null;
        String domain = null;
        String path = null;
        long expiresAt = -1;
        Duration maxAge = null;
        int version = 0;
        boolean secure = false;
        boolean httpOnly = false;
        boolean hostOnly = true;
        boolean persistent = false;

        // 循环遍历 Set-Cookie 字符串中的所有键值对
        for (int i = 0; i < nameValuePairs.length; i++) {
            final String pair = nameValuePairs[i].trim();
            final String[] nameValue = pair.split("=", 2);
            final String name = nameValue[0].trim();
            final String value = nameValue.length > 1 ? nameValue[1].trim() : "";
            if (i == 0) {
                if (StringUtils.isEmpty(value) && !pair.contains("=")) {
                    return null;
                }
                cookieName = name;
                cookieValue = URLDecoder.decode(value, StandardCharsets.UTF_8);
            } else if ("Max-Age".equalsIgnoreCase(name)) {
                final long maxAgeValue = parseMaxAge(value);
                maxAge = maxAgeValue < 0 ? null : Duration.ofSeconds(maxAgeValue);
                persistent = true;
            } else if ("Expires".equalsIgnoreCase(name)) {
                try {
                    final Date date = parseExpires(value);
                    if (date != null) {
                        expiresAt = date.getTime();
                        persistent = true;
                    }
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
            } else if ("Comment".equalsIgnoreCase(name)) {
                comment = value;
            } else if ("HttpOnly".equalsIgnoreCase(name)) {
                httpOnly = true;
            } else if ("HostOnly".equalsIgnoreCase(name)) {
                hostOnly = true;
            }
        }

        if (url != null) {
            String urlHost = url.getHost();
            if (domain == null) {
                domain = urlHost;
            }
            if (!URLUtils.matchDomain(urlHost, domain)) {
                return null;
            }
        }
        
        if (path == null) {
            path = "/";
        }

        // 如果同时有 Max-Age 和 Expires，则 Max-Age 的优先级更高
        // 若只有 Expires，则把 Expires 转换成当前时刻距离过期时间的毫秒数，并存到 maxAge 中
        if (maxAge == null && expiresAt != -1) {
            final long maxAgeValue = expiresAt - currentTimeMillis;
            if (maxAgeValue >= 0) {
                maxAge = Duration.ofMillis(maxAgeValue);
            }
        }

        return new ForestCookie(
                cookieName,
                cookieValue,
                new Date(currentTimeMillis),
                maxAge,
                comment,
                domain,
                path,
                version,
                secure,
                httpOnly,
                hostOnly,
                persistent);
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

    private static Date parseExpires(String s) {
        if (s.length() > 4 && s.charAt(3) == ',') {
            try {
                return EXPIRES_DATE_FORMAT.parse(s);
            } catch (ParseException e) {
                try {
                    return EXPIRES_DATE_FORMAT2.parse(s);
                } catch (ParseException ex) {
                    try {
                        return EXPIRES_DATE_FORMAT3.parse(s);
                    } catch (ParseException exc) {
                        try {
                            return EXPIRES_DATE_FORMAT4.parse(s);
                        } catch (ParseException parseException) {
                            return null;
                        }
                    }
                }
            }
        }
        return null;
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

    /**
     * 获取过期时间
     *
     * @return 毫秒数，过期时间
     */
    public long getExpiresTime() {
        if (maxAge == null) {
            return -1L;
        }
        return createTime.getTime() + maxAge.toMillis();
    }

    /**
     * 获取过期时间
     *
     * @return 日期对象，过期时间
     */
    public Date getExpires() {
        if (maxAge == null) {
            return null;
        }
        return new Date(getExpiresTime());
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

        if (version != 0) {
            result.append("; Version=").append(version);
        }

        return result.toString();
    }

    @Override
    public int hashCode() {
        return HashUtil.javaDefaultHash(domain + ";" + path + ";" + name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ForestCookie)) {
            return false;
        }
        final ForestCookie other = (ForestCookie) obj;
        return other.domain.equals(domain) &&
                other.path.equals(path) &&
                other.name.equals(name);
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
