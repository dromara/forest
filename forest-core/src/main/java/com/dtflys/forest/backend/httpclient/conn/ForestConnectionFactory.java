package com.dtflys.forest.backend.httpclient.conn;

import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestProxyType;
import com.dtflys.forest.http.ForestRequest;
import org.apache.http.HttpHost;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

public class ForestConnectionFactory extends PlainConnectionSocketFactory {

    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        final ForestRequest<?> currentRequest = HttpContextUtils.getCurrentRequest(context);
        final ForestProxy proxy = currentRequest.getProxy();
        if (proxy != null && proxy.getType() == ForestProxyType.SOCKS) {
            return new Socket(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.getHost(), proxy.getPort())));
        }
        return super.createSocket(context);
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
        final ForestRequest<?> currentRequest = HttpContextUtils.getCurrentRequest(context);
        final ForestProxy proxy = currentRequest.getProxy();
        final InetSocketAddress address = proxy != null && proxy.getType() == ForestProxyType.SOCKS
                ? InetSocketAddress.createUnresolved(host.getHostName(), host.getPort())
                : remoteAddress;
        return super.connectSocket(connectTimeout, socket, host, address, localAddress, context);
    }
}
