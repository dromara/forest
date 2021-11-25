package com.dtflys.forest.ssl;

import com.dtflys.forest.Forest;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;
import org.springframework.core.io.ClassPathResource;

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
            return (SSLSocketFactoryBuilder) Forest.config().getForestObjectFactory().getObject(clazz);
        } catch (ClassNotFoundException e) {
            throw new ForestRuntimeException(e);
        }
    }

    public SpringSSLKeyStore(String id, String keystoreType, String filePath, String keystorePass, String certPass, String sslSocketFactoryBuilderClass) {
        super(id, keystoreType, filePath, keystorePass, certPass, getSSLSocketFactoryBuilder(sslSocketFactoryBuilderClass));
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
