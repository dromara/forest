package org.forest.spring.ssl;

import org.forest.exceptions.ForestRuntimeException;
import org.forest.ssl.SSLKeyStore;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-31 18:55
 */
public class SpringSSLKeyStore extends SSLKeyStore {

    public SpringSSLKeyStore(String id, String keystoreType, String filePath, String keystorePass, String certPass) {
        super(id, keystoreType, filePath, keystorePass, certPass);
    }

    @Override
    public void init() {
        if (filePath.indexOf(":/") == 1 ||
                filePath.indexOf(":\\") == 1 ||
                filePath.startsWith("/")) {
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
