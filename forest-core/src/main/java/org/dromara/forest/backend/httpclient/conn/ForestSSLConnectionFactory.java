package org.dromara.forest.backend.httpclient.conn;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.ssl.SSLKeyStore;
import org.apache.http.HttpHost;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
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
    public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpContext context) throws IOException {
        ForestRequest currentRequest = getCurrentRequest(context);
        Socket connectSocket = getSslConnectionSocketFactory(currentRequest).connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
        return connectSocket;
    }

    @Override
    public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
        ForestRequest currentRequest = getCurrentRequest(context);
        Socket connectSocket = getSslConnectionSocketFactory(currentRequest).createLayeredSocket(socket, target, port, context);
        return connectSocket;
    }

    private String[][] getSupportedSSLProtocolsAndCipherSuites(ForestRequest request) {
        try {
            SSLSocketFactory sslFactory = request.getSSLSocketFactory();
            SSLSocket socket = (SSLSocket) sslFactory.createSocket();
            String[] protocols = socket.getSupportedProtocols();
            String[] cipherSuites = socket.getSupportedCipherSuites();
            return new String[][] { protocols, cipherSuites };
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    /**
     * 根据当前请求构建独立的SSLConnectionSocketFactory
     *
     * @param request 当前请求
     * @return
     */
    private SSLConnectionSocketFactory getSslConnectionSocketFactory(ForestRequest request) {
        SSLSocketFactory sslSocketFactory = request.getSSLSocketFactory();
        SSLKeyStore keyStore = request.getKeyStore();
        String[][] protocolsAndCipherSuites = getSupportedSSLProtocolsAndCipherSuites(request);
        SSLConnectionSocketFactory factory =
                new SSLConnectionSocketFactory(
                        sslSocketFactory,
                        keyStore == null ? protocolsAndCipherSuites[0] : keyStore.getProtocols(),
                        keyStore == null ? protocolsAndCipherSuites[1] : keyStore.getCipherSuites(),
                        request.hostnameVerifier());
        return factory;
    }

    private ForestRequest getCurrentRequest(HttpContext context) {
        Object request = context.getAttribute("REQUEST");
        if (request == null) {
            throw new ForestRuntimeException("Current Forest request is NULL!");
        }
        return (ForestRequest) request;
    }


}
