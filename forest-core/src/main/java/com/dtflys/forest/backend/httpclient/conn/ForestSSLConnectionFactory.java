package com.dtflys.forest.backend.httpclient.conn;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.ssl.SSLKeyStore;
import org.apache.http.HttpHost;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ForestSSLConnectionFactory implements LayeredConnectionSocketFactory {


    public ForestSSLConnectionFactory() {
    }

    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        return SocketFactory.getDefault().createSocket();
    }

    @Override
    public Socket connectSocket(
            final int connectTimeout,
            final Socket socket,
            final HttpHost host,
            final InetSocketAddress remoteAddress,
            final InetSocketAddress localAddress,
            final HttpContext context) throws IOException {
        ForestRequest request = getCurrentRequest(context);
        SSLSocketFactory sslSocketFactory = request.getSSLSocketFactory();
        SSLKeyStore keyStore = request.getKeyStore();
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(
                sslSocketFactory,
                keyStore.getProtocols(),
                keyStore.getCipherSuites(),
                request.hostnameVerifier());
        Socket connectSocket = factory.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
        return connectSocket;
    }

    private ForestRequest getCurrentRequest(HttpContext context) {
        Object request = context.getAttribute("REQUEST");
        if (request == null) {
            throw new ForestRuntimeException("Current Forest request is NULL!");
        }
        return (ForestRequest) request;
    }


    @Override
    public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) {
        return null;
    }

}
