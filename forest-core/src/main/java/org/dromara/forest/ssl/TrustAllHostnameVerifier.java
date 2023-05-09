package org.dromara.forest.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-01 18:50
 */
public class TrustAllHostnameVerifier implements HostnameVerifier {

    public final static TrustAllHostnameVerifier DEFAULT = new TrustAllHostnameVerifier();

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
