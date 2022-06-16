package com.dtflys.forest.utils;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.io.UnsupportedEncodingException;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:35
 */
public final class URLUtils {

    private URLUtils() {
    }

    /**
     * 判断字符串是否是一个URL
     *
     * @param str 被判断的字符串
     * @return {@code true}: 是URL, {@code false}: 不是URL
     */
    public static boolean isURL(String str) {
        return hasProtocol(str) || str.startsWith("file:/");
    }


    /**
     * URL字符串中是否包含HTTP协议部分
     *
     * @param url 需要被判断的URL字符串
     * @return {@code true}: 包含HTTP协议, {@code false}: 不包含
     */
    public static boolean hasProtocol(String url) {
        int len = url.length();
        char[] chars = url.toCharArray();
        for (int i = 0; i < len; i++) {
            char ch = chars[i];
            if (ch == ':') {
                String protocol = url.substring(0, i);
                if (i + 1 >= len) {
                    return false;
                }
                char nc = chars[i + 1];
                if (nc == '/') {
                    return isValidProtocol(protocol);
                }
            }
        }
        return false;
    }

    /**
     * 判断字符数组是否为合法的HTTP协议头
     *
     * @param charArray 需要被判断的字符数组
     * @return {@code true}: 是合法的HTTP协议头, {@code false}: 不是合法的HTTP协议头
     */
    private static boolean isValidProtocol(char[] charArray) {
        if (charArray.length < 1) {
            return false;
        }
        for (int i = 0; i < charArray.length; i++) {
            char ch = charArray[i];
            if (!Character.isAlphabetic(ch)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否为合法的HTTP协议头
     *
     * @param protocol 需要被判断的HTTP协议头字符串
     * @return {@code true}: 是合法的HTTP协议头, {@code false}: 不是合法的HTTP协议头
     */
    private static boolean isValidProtocol(String protocol) {
        return isValidProtocol(protocol.toCharArray());
    }

    /**
     * 判断字符串是否为合法的URL
     *
     * @param url 被判断的URL字符串
     * @return {@code true}: 合法, {@code false}: 不合法
     */
    public static boolean isValidUrl(String url) {
        return hasProtocol(url);
    }

    public static void checkBaseURL(String baseUrl) {
        if (!hasProtocol(baseUrl)) {
            throw new ForestRuntimeException("base url has no protocol: " + baseUrl);
        }
    }

    public static String getValidBaseURL(String baseUrl) {
        char[] chars = baseUrl.toCharArray();
        int len = chars.length;
        int lastIndex = len - 1;
        if ('/' == chars[lastIndex]) {
            int i = lastIndex;
            do {
                char ch = chars[i];
                if (ch != '/') {
                    break;
                }
                i--;
            } while (i > 0);
            return baseUrl.substring(0, i + 1);
        }
        return baseUrl;
    }


    /**
     * 获取合法的URL字符串
     * <p>若 uri 参数不合法，即不包含协议头以及域名/主机名/ip地址部分，则和 baseURL 参数进行合并
     * <p>若 uri 参数已经是一个合法的URL字符串，则直接返回
     *
     * @param baseURL 根URL字符串
     * @param uri URL字符串
     * @return 合法的URL字符串
     */
    public static String getValidURL(String baseURL, String uri) {
        if (StringUtils.isNotEmpty(baseURL) && !URLUtils.hasProtocol(baseURL)) {
            baseURL = "http://" + baseURL;
        }

        if (!URLUtils.hasProtocol(uri)) {
            if (StringUtils.isNotEmpty(baseURL)) {
                if (StringUtils.isBlank(uri)) {
                    return baseURL;
                }
                if (baseURL.endsWith("/")) {
                    baseURL = getValidBaseURL(baseURL);
                }
                if (!uri.startsWith("/")) {
                    uri = "/" + uri;
                }
                return baseURL + uri;
            }
            else {
                return  "http://" + uri;
            }
        }
        return uri;
    }

    /**
     * 判断端口号是否为空
     *
     * @param port 端口号
     * @return {@code true}: 空端口, {@code false}: 不为空
     */
    public static boolean isNonePort(Integer port) {
        return port == null || port < 0;
    }

    /**
     * 判断端口号是否不为空
     *
     * @param port 端口号
     * @return {@code true}: 不为空, {@code false}: 空端口
     */
    public static boolean isNotNonePort(Integer port) {
        return !isNonePort(port);
    }


    /**
     * 强制URL Encoding编码
     *
     * @param content 需要编码的原字符串
     * @param encode 编码字符集
     * @return URL Encoding编码结果字符串
     */
    public static String allEncode(String content, String encode) {
        return URLEncoder.ALL.encode(content, encode);
    }


    /**
     * 进行用户验证信息的URL Encoding编码
     *
     * @param content 需要编码的原字符串
     * @param encode 编码字符集
     * @return URL Encoding编码结果字符串
     */
    public static String userInfoEncode(String content, String encode) {
        return URLEncoder.USER_INFO.encode(content, encode);
    }


    /**
     * 进行URI路径的URL Encoding编码
     *
     * @param content 需要编码的原字符串
     * @param encode 编码字符集
     * @return URL Encoding编码结果字符串
     */
    public static String pathEncode(String content, String encode) {
        return URLEncoder.PATH.encode(content, encode);
    }

    /**
     * 进行URI ref的URL Encoding编码
     *
     * @param content 需要编码的原字符串
     * @param encode 编码字符集
     * @return URL Encoding编码结果字符串
     */
    public static String refEncode(String content, String encode) {
        return URLEncoder.PATH.encode(content, encode);
    }


    /**
     * 进行查询参数值部分的URL Encoding编码
     *
     * @param content 需要编码的原字符串
     * @param encode 编码字符集
     * @return URL Encoding编码结果字符串
     */
    public static String queryValueEncode(String content, String encode) {
        return URLEncoder.QUERY_VALUE.encode(content, encode);
    }


    /**
     * 进行URL Encoding编码
     * <p>该方法不会强制进行编码，即某些不需要编码的情况就不会去做URL Encoding
     * <p>如一个合法的URL字符串就不会被编码
     *
     * @param content 需要编码的原字符串
     * @param charset 编码字符集
     * @return URL Encoding编码结果字符串
     * @throws UnsupportedEncodingException 编码过程中可能抛出的异常
     */
    public static String encode(String content, String charset) throws UnsupportedEncodingException {
        if (content == null) {
            return null;
        }
        if (isEncoded(content)) {
            return content;
        }
        if (hasProtocol(content)) {
            String[] group = content.split("\\?");
            if (group.length > 1) {
                String query = group[1];
                String[] queries = query.split("&");
                if (queries.length > 1) {
                    return URLEncoder.PATH.encode(content, charset);
                }
            }
            return content;
        }
        return URLEncoder.PATH.encode(content, charset);
    }

    /**
     * 判断字符串是否已被URL Encoding编码过
     *
     * @param content 被判断的原字符串
     * @return {@code true}: 被编码过, {@code false}: 未被编码过
     */
    public static boolean isEncoded(String content) {
        char[] chars = content.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len - 1; i++) {
            char ch = chars[i];
            if (ch == '%') {
                char nc = chars[i + 1];
                if (Character.isDigit(nc)
                        || nc == 'A'
                        || nc == 'B'
                        || nc == 'C'
                        || nc == 'D'
                        || nc == 'E'
                        || nc == 'F') {
                    return true;
                }
            }
        }
        return false;
    }

}
