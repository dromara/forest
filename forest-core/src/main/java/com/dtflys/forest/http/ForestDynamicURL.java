package com.dtflys.forest.http;


import com.dtflys.forest.mapping.MappingURLTemplate;
import com.dtflys.forest.utils.URLUtils;

public class ForestDynamicURL extends ForestURL {

    private volatile MappingURLTemplate template;

    private volatile ForestURL renderedURL;

    public ForestDynamicURL(MappingURLTemplate template) {
        this.template = template;
    }

    private void checkAndRefreshURL() {
        if (renderedURL == null && request != null) {
            renderedURL = new ForestURL();
            template.render(renderedURL, request, request.arguments(), request.getQuery());
        }
    }

    @Override
    public String getHost() {
        final String host = super.getHost();
        if (host != null) {
            return host;
        }
        checkAndRefreshURL();
        return renderedURL.getHost();
    }

    @Override
    public int getPort() {
        if (!URLUtils.isNonePort(port)) {
            return normalizePort(port, ssl);
        } else if (address != null) {
            return normalizePort(address.getPort(), ssl);
        }
        checkAndRefreshURL();
        return renderedURL.getPort();
    }

    @Override
    public String getScheme() {
        final String scheme = super.getScheme();
        if (scheme != null) {
            return scheme;
        }
        checkAndRefreshURL();
        return renderedURL.getScheme();
    }

    @Override
    public String getPath() {
        if (path != null) {
            return path;
        }
        checkAndRefreshURL();
        return renderedURL.getPath();
    }

    @Override
    protected String getBasePath() {
        if (basePath != null) {
            return basePath;
        }
        checkAndRefreshURL();
        return renderedURL.getBasePath();
    }

    @Override
    public String toURLString() {
        return super.toURLString();
    }
}
