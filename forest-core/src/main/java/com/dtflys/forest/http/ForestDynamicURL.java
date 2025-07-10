package com.dtflys.forest.http;


import com.dtflys.forest.mapping.MappingListener;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.MappingURLTemplate;
import com.dtflys.forest.reflection.ForestVariable;
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
        if (baseURL != null) {
            final String baseUserInfo = baseURL.getUserInfo();
            if (StringUtils.isNotEmpty(baseUserInfo)) {
                return baseUserInfo;
            }
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
        if (baseURL != null) {
            final String baseHost = baseURL.getHost();
            if (StringUtils.isNotEmpty(baseHost)) {
                return baseHost;
            }
        }
        checkAndRefreshURL();
        return renderedURL.getHost();
    }

    @Override
    public int getPort() {
        if (!URLUtils.isNonePort(port)) {
            return normalizePort(port, ssl);
        } else if (address != null) {
            int addressPort = address.getPort();
            if (!URLUtils.isNonePort(addressPort)) {
                return normalizePort(addressPort, ssl);
            }
        }
        if (baseURL != null) {
            final int basePort = baseURL.getPort();
            if (!URLUtils.isNonePort(basePort)) {
                return basePort;
            }
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
            return ForestVariable.getStringValue(path, request);
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
