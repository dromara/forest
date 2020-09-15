package com.dtflys.forest.logging;

import com.dtflys.forest.http.ForestRequest;

import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-14 17:31
 */
public class RequestLogMessage {

    private ForestRequest request;

    private String type;

    private String requestLine;

    private List<HeaderMessage> headers;

    private List<String> body;

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


    public String getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(String requestLine) {
        this.requestLine = requestLine;
    }

    public List<HeaderMessage> getHeaders() {
        return headers;
    }

    public void setHeaders(List<HeaderMessage> headers) {
        this.headers = headers;
    }

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }
}
