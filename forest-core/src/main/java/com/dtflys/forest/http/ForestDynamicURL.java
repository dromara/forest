package com.dtflys.forest.http;


import com.dtflys.forest.mapping.MappingListener;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.MappingURLTemplate;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLUtils;

public class ForestDynamicURL extends ForestURL implements MappingListener {

    private final MappingURLTemplate template;

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
    public String getUserInfo() {
        if (StringUtils.isNotEmpty(userInfo)) {
            return userInfo;
        } else if (address != null) {
            return address.getUserInfo();
        }
        checkAndRefreshURL();
        return renderedURL.getUserInfo();
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

//    @Override
//    public String toURLString() {
//        if (renderedURL == null && template != null) {
//            if (request != null) {
//                checkAndRefreshURL();
//            } else {
//                return template.toString();
//            }
//        }
//        return super.toURLString();
//    }

    @Override
    public void clear() {
        this.renderedURL = null;
    }

    @Override
    public void onChanged(MappingTemplate template, Object newValue) {

    }
}
