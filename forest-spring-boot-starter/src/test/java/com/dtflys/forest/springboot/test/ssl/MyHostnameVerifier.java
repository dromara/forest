package com.dtflys.forest.springboot.test.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-12-09 1:31
 */
public class MyHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String s, SSLSession sslSession) {
        if ("gitee.com".equals(s)) {
            return true;
        }
        return false;
    }

}
