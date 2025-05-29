package com.dtflys.forest.test.http.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class MyHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String s, SSLSession sslSession) {
        System.out.println("do MyHostnameVerifier");
        if ("localhost".equals(s)) {
            return false;
        }
        return true;
    }
}
