package org.forest.utils;

import org.forest.exceptions.ForestRuntimeException;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:35
 */
public class URLUtils {


    public static boolean hasProtocal(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    public static void checkBaseURL(String baseUrl) {
        if (!hasProtocal(baseUrl)) {
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
            } while (i > 0);
            return baseUrl.substring(0, i);
        }
        return baseUrl;
    }


    public static String getValidURL(String baseURL, String uri) {
        if (!URLUtils.hasProtocal(uri)) {
            if (StringUtils.isNotEmpty(baseURL)) {
                if (baseURL.endsWith("/")) {
                    baseURL = baseURL.substring(0, baseURL.length() - 1);
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

}
