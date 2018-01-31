package org.forest.ssl;

import org.apache.commons.io.IOUtils;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * SSL keyStore
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-30 16:04
 */
public class SSLKeyStore {

    public final static String DEFAULT_KEYSTORE_TYPE = "jks";

    private final String id;

    private final String keystoreType;

    private InputStream inputStream;

    private String keystorePass;

    private volatile String fileContentCache;

    public SSLKeyStore(String id, String keystoreType) {
        this.id = id;
        this.keystoreType = keystoreType;
    }

    public String getId() {
        return id;
    }

    public String getKeystoreType() {
        return keystoreType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }


    public String getFileContent() {
        try {
            if (StringUtils.isEmpty(fileContentCache)) {
                synchronized (this) {
                    if (StringUtils.isEmpty(fileContentCache)) {
                        fileContentCache = IOUtils.toString(inputStream);
                    }
                }
            }
            return fileContentCache;
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

}
