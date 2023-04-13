package org.dromara.forest.logging;

import org.dromara.forest.http.ForestRequest;

import java.util.LinkedList;
import java.util.List;

/**
 * 请求日志消息
 * <p>封装了请求日志打印所需的所有信息</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-14 17:31
 */
public class RequestLogMessage {

    /**
     * Forest请求对象
     */
    private ForestRequest request;

    /**
     * 请求类型
     */
    private String type;

    /**
     * 请求URI地址
     */
    private String uri;

    /**
     * 请求协议
     */
    private String scheme;

    /**
     * 请求重试次数
     */
    private int retryCount;

    /**
     * 请求头列表
     */
    private List<LogHeaderMessage> headers;

    /**
     * 请求体信息
     */
    private LogBodyMessage body;

    /**
     * 请求代理信息
     */
    private RequestProxyLogMessage proxy;

    public ForestRequest getRequest() {
        return request;
    }

    public void setRequest(ForestRequest request) {
        this.request = request;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTypeChangeHistory() {
        return request.getTypeChangeHistoryString();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme.toUpperCase();
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * 获取请求行
     * @return 请求行字符串
     */
    public String getRequestLine() {
        return getType() + " " + getUri() + " " + getScheme();
    }

    public List<LogHeaderMessage> getHeaders() {
        return headers;
    }

    public void setHeaders(List<LogHeaderMessage> headers) {
        this.headers = headers;
    }

    public void addHeader(LogHeaderMessage headerMessage) {
        if (this.headers == null) {
            this.headers = new LinkedList<>();
        }
        this.headers.add(headerMessage);
    }

    public LogBodyMessage getBody() {
        return body;
    }

    public void setBody(LogBodyMessage body) {
        this.body = body;
    }

    public RequestProxyLogMessage getProxy() {
        return proxy;
    }

    public void setProxy(RequestProxyLogMessage proxy) {
        this.proxy = proxy;
    }
}
