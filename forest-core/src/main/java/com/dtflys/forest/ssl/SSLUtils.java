package com.dtflys.forest.ssl;

import com.dtflys.forest.utils.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;

import javax.net.ssl.*;
import java.security.*;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-01 18:59
 */
public class SSLUtils {

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
        final SSLKeyStore fKeyStore = request.getKeyStore();
        final KeyStore keyStore = fKeyStore.getTrustStore();
        final String certPass = fKeyStore.getCertPass();
        if (keyStore != null) {
            try {
                 //密钥库
                char[] certPassCharArray = certPass.toCharArray();
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("sunx509");
                kmf.init(keyStore, certPassCharArray);

                TrustManagerFactory tmf = TrustManagerFactory
                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);

                SSLContextBuilder scBuilder = SSLContexts.custom();
                String protocol = request.getSslProtocol();
                if (StringUtils.isNotEmpty(protocol)) {
                    scBuilder.useProtocol(protocol);
                }
                scBuilder.loadTrustMaterial(keyStore, new TrustSelfSignedStrategy());
                if (certPass != null) {
                    scBuilder.loadKeyMaterial(keyStore, certPassCharArray);
                }
                sslContext = scBuilder.build();
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
        SSLContext sc;
        if (StringUtils.isEmpty(sslProtocol)) {
            sc = SSLContexts.custom().build();
        } else {
            sc = SSLContext.getInstance(sslProtocol);
            TrustAllManager trustManager = new TrustAllManager();
            sc.init(null, new TrustManager[] { trustManager }, null);
        }
        return sc;
    }


    /**
     * 获取SSL上下文
     *
     * @param request Forest请求对象，{@link ForestRequest}类实例
     * @param protocol 协议名称
     * @return SSL上下文，{@link SSLContext}类实例
     * @throws KeyManagementException Key管理异常
     * @throws NoSuchAlgorithmException 找不到对应加密算法的异常
     */
    public static SSLContext getSSLContext(ForestRequest request, String protocol) throws KeyManagementException, NoSuchAlgorithmException {
        if (request.getKeyStore() == null) {
            return SSLUtils.createIgnoreVerifySSL(protocol);
        }
        return SSLUtils.customSSL(request);
    }

    public static SSLSocketFactory getDefaultSSLSocketFactory(ForestRequest request, String protocol) {
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

}
