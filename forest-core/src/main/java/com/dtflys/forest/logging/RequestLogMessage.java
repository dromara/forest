package com.dtflys.forest.logging;

import com.dtflys.forest.http.ForestRequest;

public class RequestLogMessage {

    private ForestRequest request;

    private String type;

    private String url;

    private String protocol;

    private String header;

    private String body;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
