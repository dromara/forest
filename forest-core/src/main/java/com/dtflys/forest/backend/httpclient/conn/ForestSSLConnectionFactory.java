package com.dtflys.forest.backend.httpclient.conn;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.ssl.SSLUtils;
import okhttp3.CertificatePinner;
import okhttp3.internal.tls.OkHostnameVerifier;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpHost;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

//import org.apache.http.annotation.ThreadSafe;

public class ForestSSLConnectionFactory implements LayeredConnectionSocketFactory {

    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifier();

    private final static ThreadLocal<ForestRequest> REQUEST_LOCAL = new ThreadLocal<>();

    private SSLSocket sslSocket;



    private static String[] split(String s) {
        return TextUtils.isBlank(s)?null:s.split(" *, *");
    }


    public ForestSSLConnectionFactory() {
    }

    protected void prepareSocket(SSLSocket socket) throws IOException {
    }

    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        return SocketFactory.getDefault().createSocket();
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
        Args.notNull(host, "HTTP host");
        Args.notNull(remoteAddress, "Remote address");
        Socket sock = socket != null?socket:this.createSocket(context);
        if(localAddress != null) {
            sock.bind(localAddress);
        }

        try {
            sock.connect(remoteAddress, connectTimeout);
        } catch (IOException var11) {
            try {
                sock.close();
            } catch (IOException var10) {
                ;
            }

            throw var11;
        }

        if(sock instanceof SSLSocket) {
            SSLSocket sslSocket = (SSLSocket)sock;
            sslSocket.startHandshake();
            return sock;
        } else {
            return this.createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), context);
        }
    }


    private ForestRequest getCurrentRequest() {
        ForestRequest request = REQUEST_LOCAL.get();
        return request;
    }


    public void setCurrentRequest(ForestRequest request) {
        REQUEST_LOCAL.set(request);
    }


    public ForestRequest removeCurrentRequest() {
        ForestRequest request = REQUEST_LOCAL.get();
        REQUEST_LOCAL.remove();
        return request;
    }

    @Override
    public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
        ForestRequest request = getCurrentRequest();
        if (request == null) {
            throw new ForestRuntimeException("Current request is NULL!");
        }

        SSLSocketFactory sslSocketFactory = request.getSSLSocketFactory();
        this.sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, target, port, true);

        if (request != null) {
            SSLKeyStore keyStore = request.getKeyStore();
            if (keyStore != null) {
                String[] protocols = keyStore.getProtocols();
                String[] cipherSuites = keyStore.getCipherSuites();
                if (protocols != null) {
                    sslSocket.setEnabledProtocols(protocols);
                }
                if (cipherSuites != null) {
                    sslSocket.setEnabledCipherSuites(cipherSuites);
                }
            }
        }
        this.prepareSocket(sslSocket);
        this.sslSocket.startHandshake();
        return sslSocket;
    }


    public void verifyHostname(ForestRequest request, String hostname) throws SSLPeerUnverifiedException {
        if (sslSocket == null) {
            return;
        }
        SSLSession session = this.sslSocket.getSession();
        if (!request.hostnameVerifier().verify(hostname, sslSocket.getSession())) {
            Certificate[] certs = session.getPeerCertificates();
            if (ArrayUtils.isEmpty(certs)) {
                X509Certificate cert = (X509Certificate) certs[0];
                throw new SSLPeerUnverifiedException(
                        "Hostname " + hostname + " not verified:"
                                + "\n    certificate: " + CertificatePinner.pin(cert)
                                + "\n    DN: " + cert.getSubjectDN().getName()
                                + "\n    subjectAltNames: " + OkHostnameVerifier.allSubjectAltNames(cert));
            } else {
                throw new SSLPeerUnverifiedException(
                        "Hostname " + hostname + " not verified (no certificates)");
            }
        }

    }
}
