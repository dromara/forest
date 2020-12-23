package com.dtflys.forest.ssl;

import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;

import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-01 18:59
 */
public class SSLUtils {

//    public final static String DEFAULT_PROTOCOL = "SSLv3"; // 已弃用
    public final static String SSL_2 = "SSLv2";
    public final static String SSL_3 = "SSLv3";
    public final static String TLS_1_0 = "TLSv1.0";
    public final static String TLS_1_1 = "TLSv1.1";
    public final static String TLS_1_2 = "TLSv1.2";
    public final static String TLS_1_3 = "TLSv1.3";

    /**
     * 自定义SSL证书
     * @param request Forest请求对象，{@link ForestRequest}类实例
     * @return SSL上下文，{@link SSLContext}类实例
     */
    public static SSLContext customSSL(ForestRequest request) {
        SSLContext sslContext = null;
        SSLKeyStore fKeyStore = request.getKeyStore();
        final KeyStore keyStore = fKeyStore.getTrustStore();
        String certPass = fKeyStore.getCertPass();
        if (keyStore != null) {
            try {

//                sslContext = SSLContext.getInstance("TLS");

                //密钥库
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("sunx509");
                kmf.init(keyStore, certPass.toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory
                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);

//                X509TrustManager defaultTrustManager = (X509TrustManager) tmf
//                        .getTrustManagers()[0];
//                SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
//                sslContext.init(kmf.getKeyManagers(), new TrustManager[] { tm }, null);
//                sslContext.init(null, tmf.getTrustManagers(), null);

//                sslContext =
//                        SSLContexts.custom().useSSL().loadKeyMaterial(keyStore, certPass.toCharArray())
//                                .loadTrustMaterial(keyStore).build();
//                sslContext.init(kmf.getKeyManagers(), new TrustManager[] { tm }, null);

                SSLContextBuilder scBuilder = SSLContexts.custom();
                if (certPass != null) {
                    sslContext = scBuilder
                            .loadKeyMaterial(keyStore, certPass.toCharArray())
                            .build();
                } else {
                    sslContext = scBuilder
                            .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
                            .build();
                }
            } catch (NoSuchAlgorithmException e) {
                throw new ForestRuntimeException(e);
            } catch (KeyManagementException e) {
                throw new ForestRuntimeException(e);
            } catch (KeyStoreException e) {
                throw new ForestRuntimeException(e);
            } catch (UnrecoverableKeyException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return sslContext;
    }


    /**
     * 默认的单向验证HTTPS请求绕过SSL验证，使用默认SSL协议
     *
     * @param sslProtocol SSL协议名称
     * @return SSL上下文，{@link SSLContext}类实例
     * @throws NoSuchAlgorithmException 没有对应加密算法异常
     * @throws KeyManagementException Key管理异常
     */
    public static SSLContext createIgnoreVerifySSL(String sslProtocol) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance(sslProtocol);
        TrustAllManager trustManager = new TrustAllManager();
        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }



    /**
     * 获取SSL上下文
     * @param request Forest请求对象，{@link ForestRequest}类实例
     * @param protocol 协议名称
     * @return SSL上下文，{@link SSLContext}类实例
     */
    public static SSLContext getSSLContext(ForestRequest request, String protocol) throws KeyManagementException, NoSuchAlgorithmException {
        if (request.getKeyStore() == null) {
            return SSLUtils.createIgnoreVerifySSL(protocol);
        }
        return SSLUtils.customSSL(request);
    }

    public static SSLSocketFactory getSSLSocketFactory(ForestRequest request, String protocol) {
        if (request == null) {
            return null;
        }
        try {
            SSLContext sslContext = SSLUtils.getSSLContext(request, protocol);
            if (sslContext == null) {
                throw new ForestRuntimeException("SSL context cannot be initialized.");
            }
            return sslContext.getSocketFactory();
        } catch (KeyManagementException e) {
            throw new ForestRuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new ForestRuntimeException(e);
        }
    }

/*
    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }
*/

}
