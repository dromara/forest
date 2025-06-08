package com.dtflys.forest.http;

import java.util.concurrent.atomic.AtomicInteger;

public class ForestURLHolder extends ForestURLBuilder {

    private volatile ForestRequest request;

    private volatile String template;

    private volatile ForestURL currentURL;

    private AtomicInteger lastVersion = new AtomicInteger(0);

    private AtomicInteger currentVersion = new AtomicInteger(0);

    public ForestURLHolder(String url) {
        this.template = url;
    }

    public ForestRequest getRequest() {
        return request;
    }

    public ForestURLBuilder setRequest(ForestRequest request) {
        this.request = request;
        currentVersion.incrementAndGet();
        return this;
    }

    private boolean isChanged() {
        return currentVersion.get() > lastVersion.get();
    }

    private void refreshURL() {
        if (isChanged()) {
            if (request != null) {


            }
        }
    }

    public String getUrl() {
        if (request == null || currentURL == null) {
            return template;
        }
        return template;
    }

    @Override
    public String getScheme() {
        return super.getScheme();
    }

    @Override
    public ForestURLBuilder setScheme(String scheme) {
        return super.setScheme(scheme);
    }

    @Override
    public String getHost() {
        return super.getHost();
    }

    @Override
    public ForestURLBuilder setHost(String host) {
        return super.setHost(host);
    }

    @Override
    public Integer getPort() {
        return super.getPort();
    }

    @Override
    public ForestURLBuilder setPort(Integer port) {
        return super.setPort(port);
    }

    @Override
    public String getBasePath() {
        return super.getBasePath();
    }

    @Override
    public ForestURLBuilder setBasePath(String basePath) {
        return super.setBasePath(basePath);
    }

    @Override
    public String getPath() {
        return super.getPath();
    }

    @Override
    public ForestURLBuilder setPath(String path) {
        return super.setPath(path);
    }

    @Override
    public String getUserInfo() {
        return super.getUserInfo();
    }

    @Override
    public ForestURLBuilder setUserInfo(String userInfo) {
        return super.setUserInfo(userInfo);
    }

    @Override
    public String getRef() {
        return super.getRef();
    }

    @Override
    public ForestURLBuilder setRef(String ref) {
        return super.setRef(ref);
    }

    @Override
    public boolean isSsl() {
        return super.isSsl();
    }

    @Override
    public ForestURLBuilder setSsl(boolean ssl) {
        return super.setSsl(ssl);
    }

    @Override
    public ForestURL build() {
        return super.build();
    }
}
