package com.dtflys.forest.ssl;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * SSL keyStore
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-30 16:04
 */
public class SSLKeyStore {

    public final static String DEFAULT_KEYSTORE_TYPE = "jks";

    protected final String id;

    protected final String keystoreType;

    protected String filePath;

    protected InputStream inputStream;

    protected String keystorePass;

    protected String certPass;

    protected KeyStore trustStore;

    protected String[] protocols;

    protected String[] cipherSuites;

    protected SSLSocketFactoryBuilder sslSocketFactoryBuilder;

    public SSLKeyStore(String id, String filePath, String keystorePass, String certPass, SSLSocketFactoryBuilder sslSocketFactoryBuilder) {
        this(id, DEFAULT_KEYSTORE_TYPE, filePath, keystorePass, certPass, sslSocketFactoryBuilder);
    }

    public SSLKeyStore(String id, String keystoreType, String filePath, String keystorePass, String certPass, SSLSocketFactoryBuilder sslSocketFactoryBuilder) {
        this.id = id;
        this.keystoreType = keystoreType;
        this.filePath = filePath;
        this.keystorePass = keystorePass;
        this.certPass = certPass;
        this.sslSocketFactoryBuilder = sslSocketFactoryBuilder;
        init();
        loadTrustStore();
    }

    public String getId() {
        return id;
    }

    public String getKeystoreType() {
        return keystoreType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String[] getProtocols() {
        return protocols;
    }

    public void setProtocols(String[] protocols) {
        this.protocols = protocols;
    }

    public String[] getCipherSuites() {
        return cipherSuites;
    }

    public void setCipherSuites(String[] cipherSuites) {
        this.cipherSuites = cipherSuites;
    }

    public SSLSocketFactoryBuilder getSslSocketFactoryBuilder() {
        return sslSocketFactoryBuilder;
    }

    public void setSslSocketFactoryBuilder(SSLSocketFactoryBuilder sslSocketFactoryBuilder) {
        this.sslSocketFactoryBuilder = sslSocketFactoryBuilder;
    }

    public void init() {
        String path = filePath.trim();
        File file = new File(path);
        if (!file.exists()) {
            java.net.URL url = getClass().getClassLoader().getResource(path);
            if (url == null) {
                throw new ForestRuntimeException(
                        "The file of SSL KeyStore \"" + id + "\" " + filePath + " cannot be found!");
            }
            path = url.getFile();
            file = new File(path);
            if (!file.exists()) {
                throw new ForestRuntimeException(
                        "The file of SSL KeyStore \"" + id + "\" " + filePath + " cannot be found!");
            }
        }
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ForestRuntimeException(
                    "An error occurred while reading he file of SSL KeyStore \"\" + id + \"\"", e);
        }
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public String getCertPass() {
        return certPass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    public void loadTrustStore() {
        if (inputStream != null) {
            try {
                trustStore = KeyStore.getInstance(keystoreType);
                String pass = this.keystorePass;
                if (pass == null) {
                    trustStore.load(inputStream, null);
                }
                else {
                    trustStore.load(inputStream, pass.trim().toCharArray());
                }
            } catch (KeyStoreException e) {
                throw new ForestRuntimeException(e);
            } catch (CertificateException e) {
                throw new ForestRuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new ForestRuntimeException(e);
            } catch (IOException e) {
                throw new ForestRuntimeException(e);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public KeyStore getTrustStore() {
        return trustStore;
    }


}
