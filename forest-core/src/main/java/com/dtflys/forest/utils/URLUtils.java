package com.dtflys.forest.utils;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:35
 */
public final class URLUtils {

    private URLUtils() {
    }

    public static boolean isURL(String str) {
        return hasProtocol(str) || str.startsWith("file:/");
    }

    public static boolean hasProtocol(String url) {
        int len = url.length();
        for (int i = 0; i < len; i++) {
            char ch = url.charAt(i);
            if (ch == ':') {
                String protocol = url.substring(0, i);
                char nc = url.charAt(i + 1);
                if (nc == '/') {
                    return isValidProtocol(protocol);
                }
            }
        }
        return false;
    }

    private static boolean isValidProtocol(String protocol) {
        int len = protocol.length();
        if (len < 1) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char ch = protocol.charAt(i);
            if (!Character.isAlphabetic(ch)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidUrl(String url) {
        return hasProtocol(url);
    }

    public static void checkBaseURL(String baseUrl) {
        if (!hasProtocol(baseUrl)) {
            throw new ForestRuntimeException("base url has no protocol: " + baseUrl);
        }
    }

    public static String getValidBaseURL(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            int i = baseUrl.length() - 1;
            do {
                char ch = baseUrl.charAt(i);
                if (ch != '/') {
                    break;
                }
                i--;
            } while (i > 0);
            return baseUrl.substring(0, i + 1);
        }
        return baseUrl;
    }


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

    public static String urlEncoding(String query, String encode) throws UnsupportedEncodingException {
        if (isURL(query)) {
            return query;
        }
        return URLEncoder.encode(query, encode);
    }

}
