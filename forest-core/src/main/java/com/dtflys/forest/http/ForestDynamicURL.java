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
        if (renderedURL == null) {
            renderedURL = new ForestURL();
            template.render(renderedURL, request, request.arguments(), request.getQuery());
        }
    }

    @Override
    public String getHost() {
        if (host != null) {
            return host;
        }
        checkAndRefreshURL();
        return renderedURL.getHost();
    }

    @Override
    public int getPort() {
        if (!URLUtils.isNonePort(port)) {
            return port;
        }
        checkAndRefreshURL();
        return renderedURL.getPort();
    }

    @Override
    public String getScheme() {
        if (scheme != null) {
            return scheme;
        }
        checkAndRefreshURL();
        return renderedURL.getScheme();
    }
}
