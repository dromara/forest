package com.dtflys.forest.ssl;

import com.dtflys.forest.Forest;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.TrustManager;
import java.io.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-31 18:55
 */
public class SpringSSLKeyStore extends SSLKeyStore {

    private static SSLSocketFactoryBuilder getSSLSocketFactoryBuilder(String sslSocketFactoryBuilderClass) {
        if (StringUtils.isBlank(sslSocketFactoryBuilderClass)) {
            return null;
        }
        try {
            Class clazz = Class.forName(sslSocketFactoryBuilderClass);
            if (!SSLSocketFactoryBuilder.class.isAssignableFrom(clazz)) {
                throw new ForestRuntimeException("[Forest] The value of property 'sslSocketFactoryBuilder' must be a class inherited from com.dtflys.forest.ssl.SSLSocketFactoryBuilder");
            }
            return (SSLSocketFactoryBuilder) Forest.config().getForestObjectFactory().getObject(clazz);
        } catch (ClassNotFoundException e) {
            throw new ForestRuntimeException(e);
        }
    }

    private static TrustManager getTrustManager(String trustManagerClass) {
        if (StringUtils.isBlank(trustManagerClass)) {
            return null;
        }
        try {
            Class clazz = Class.forName(trustManagerClass);
            if (!TrustManager.class.isAssignableFrom(clazz)) {
                throw new ForestRuntimeException("[Forest] The value of property 'trustManager' must be a class inherited from javax.net.ssl.TrustManager");
            }
            return (TrustManager) Forest.config().getForestObjectFactory().getObject(clazz);
        } catch (ClassNotFoundException e) {
            throw new ForestRuntimeException(e);
        }
    }

    private static HostnameVerifier getHostnameVerifier(String hostnameVerifierClass) {
        if (StringUtils.isBlank(hostnameVerifierClass)) {
            return null;
        }
        try {
            Class clazz = Class.forName(hostnameVerifierClass);
            if (!HostnameVerifier.class.isAssignableFrom(clazz)) {
                throw new ForestRuntimeException("[Forest] The value of property 'hostnameVerifier' must be a class inherited from javax.net.ssl.HostnameVerifier");
            }
            return (HostnameVerifier) Forest.config().getForestObjectFactory().getObject(clazz);
        } catch (ClassNotFoundException e) {
            throw new ForestRuntimeException(e);
        }
    }

    /**
     * Spring SSL Key Store 构造函数
     *
     * @param id KeyStore Id
     * @param keystoreType  KeyStore类型
     * @param filePath 证书文件路径
     * @param keystorePass KeyStore密码
     * @param certPass 验证密码
     * @param hostnameVerifierClass 域名验证器类
     * @param sslSocketFactoryBuilderClass SSL上下文工厂构造器类
     */
    public SpringSSLKeyStore(
            String id,
            String keystoreType,
            String filePath,
            String keystorePass,
            String certPass,
            String trustManagerClass,
            String hostnameVerifierClass,
            String sslSocketFactoryBuilderClass) {
        super(
                id,
                keystoreType,
                filePath,
                keystorePass,
                certPass,
                getTrustManager(trustManagerClass),
                getHostnameVerifier(hostnameVerifierClass),
                getSSLSocketFactoryBuilder(sslSocketFactoryBuilderClass));
    }

    @Override
    public void init() {
        if (StringUtils.isBlank(filePath)) {
            return;
        }
        if (filePath.indexOf(":/") == 1
                || filePath.indexOf(":\\") == 1
                || filePath.startsWith("/")) {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new ForestRuntimeException(
                        "The file of SSL KeyStore \"" + id + "\" " + filePath + " cannot be found!");
            }
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new ForestRuntimeException(
                        "An error occurred while reading he file of SSL KeyStore \"\" + id + \"\"", e);
            }
        }
        else {
            ClassPathResource resource = new ClassPathResource(filePath);
            if (!resource.exists()) {
                throw new ForestRuntimeException(
                        "The file of SSL KeyStore \"" + id + "\" " + filePath + " cannot be found!");
            }
            try {
                inputStream = resource.getInputStream();
            } catch (IOException e) {
                throw new ForestRuntimeException(
                        "An error occurred while reading he file of SSL KeyStore \"\" + id + \"\"", e);
            }
        }
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }
}
