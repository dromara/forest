package com.dtflys.forest.backend.httpclient.conn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.*;
import javax.net.ssl.SSLSocketFactory;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.ssl.SSLUtils;
import org.apache.http.HttpHost;
//import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

public class ForestSSLConnectionFactory implements LayeredConnectionSocketFactory {

    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifier();

    private final static ThreadLocal<ForestRequest> REQUEST_LOCAL = new ThreadLocal<>();
    private final X509HostnameVerifier hostnameVerifier;

    public static org.apache.http.conn.ssl.SSLConnectionSocketFactory getSocketFactory() throws SSLInitializationException {
        return new org.apache.http.conn.ssl.SSLConnectionSocketFactory(SSLContexts.createDefault(), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }

    private static String[] split(String s) {
        return TextUtils.isBlank(s)?null:s.split(" *, *");
    }

    public static org.apache.http.conn.ssl.SSLConnectionSocketFactory getSystemSocketFactory() throws SSLInitializationException {
        return new org.apache.http.conn.ssl.SSLConnectionSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault(), split(System.getProperty("https.protocols")), split(System.getProperty("https.cipherSuites")), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }

    public ForestSSLConnectionFactory() {
        this(BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }


    public ForestSSLConnectionFactory(X509HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier != null?hostnameVerifier:BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
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
            SSLSocket sslsock = (SSLSocket)sock;
            sslsock.startHandshake();
            this.verifyHostname(sslsock, host.getHostName());
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
        SSLSocket sslsock = (SSLSocket) SSLUtils.getSSLSocketFactory(request, request.getSslProtocol())
                .createSocket(socket, target, port, true);
        if (request != null) {
            SSLKeyStore keyStore = request.getKeyStore();
            if (keyStore != null) {
                String[] protocols = keyStore.getProtocols();
                String[] cipherSuites = keyStore.getCipherSuites();
                if (protocols != null) {
                    sslsock.setEnabledProtocols(protocols);
                }
                if (cipherSuites != null) {
                    sslsock.setEnabledCipherSuites(cipherSuites);
                }
            }
        }
        this.prepareSocket(sslsock);
        sslsock.startHandshake();
        this.verifyHostname(sslsock, target);
        return sslsock;
    }


    private void verifyHostname(SSLSocket sslsock, String hostname) throws IOException {
        try {
            this.hostnameVerifier.verify(hostname, sslsock);
        } catch (IOException var6) {
            try {
                sslsock.close();
            } catch (Exception var5) {
                ;
            }

            throw var6;
        }
    }
}
