package org.forest.ssl;

import java.io.InputStream;

/**
 * SSL keyStore
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-30 16:04
 */
public class ForestKeyStore {

    private InputStream inputStream;

    private String keystorePass;

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
}
