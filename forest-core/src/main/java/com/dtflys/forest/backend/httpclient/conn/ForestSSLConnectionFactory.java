package com.dtflys.forest.backend.httpclient.conn;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.ssl.SSLUtils;
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
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

//import org.apache.http.annotation.ThreadSafe;

public class ForestSSLConnectionFactory implements LayeredConnectionSocketFactory {

    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifier();

    private final static ThreadLocal<ForestRequest> REQUEST_LOCAL = new ThreadLocal<>();
    private final HostnameVerifier hostnameVerifier;


    private static String[] split(String s) {
        return TextUtils.isBlank(s)?null:s.split(" *, *");
    }


    public ForestSSLConnectionFactory() {
        this(BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }


    public ForestSSLConnectionFactory(HostnameVerifier hostnameVerifier) {
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
            this.verifyHostname(host.getHostName(), sslsock);
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

        try {
            SSLContext sslContext = SSLUtils.getSSLContext(request, request.getSslProtocol());
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
        } catch (KeyManagementException e) {
            throw new ForestRuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new ForestRuntimeException(e);
        }


        SSLSocketFactory sslSocketFactory = SSLUtils.getSSLSocketFactory(request, request.getSslProtocol());


        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, target, port, true);


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
        sslSocket.startHandshake();
        this.verifyHostname(target, sslSocket);
        return sslSocket;
    }




    private void verifyHostname(String hostname, SSLSocket sslSocket) {
        this.hostnameVerifier.verify(hostname, sslSocket.getSession());
    }
}
