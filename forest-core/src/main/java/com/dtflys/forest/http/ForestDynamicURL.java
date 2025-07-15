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
            template.render(renderedURL, request, request.arguments(), query);
        }
    }

    @Override
    public ForestQueryMap getQuery() {
        checkAndRefreshURL();
        return query;
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
        final String hostStr = getHost(ForestVariable.getStringValue(host, request));
        if (StringUtils.isNotEmpty(hostStr)) {
            return hostStr;
        }
        checkAndRefreshURL();
        final String renderedHost = ForestVariable.getStringValue(renderedURL.host, request);
        if (StringUtils.isNotEmpty(renderedHost)) {
            return renderedHost;
        }
        if (address != null) {
            final String addressHost = address.getHost();
            if (StringUtils.isNotEmpty(addressHost)) {
                return addressHost;
            }
        }
        if (baseURL != null) {
            final String baseHost = baseURL.getHost();
            if (StringUtils.isNotEmpty(baseHost)) {
                return baseHost;
            }
        }
        if (baseAddress != null) {
            final String baseAddressHost = baseAddress.getHost();
            if (StringUtils.isNotEmpty(baseAddressHost)) {
                return baseAddressHost;
            }
        }
        return renderedHost;
    }

    @Override
    public int getPort() {
        final Integer portInt = ForestVariable.getIntegerValue(port, request);
        if (URLUtils.isNotNonePort(portInt)) {
            return normalizePort(portInt, ssl);
        }
        checkAndRefreshURL();
        final Integer renderedPort = ForestVariable.getIntegerValue(renderedURL.port, request);
        if (URLUtils.isNotNonePort(renderedPort)) {
            return renderedPort;
        }
        if (address != null) {
            int addressPort = address.getPort();
            if (URLUtils.isNotNonePort(addressPort)) {
                return normalizePort(addressPort, ssl);
            }
        }
        if (baseURL != null) {
            final int basePort = baseURL.getPort();
            if (URLUtils.isNotNonePort(basePort)) {
                return basePort;
            }
        }
        if (baseAddress != null) {
            final int basePort = baseAddress.getPort();
            if (URLUtils.isNotNonePort(basePort)) {
                return normalizePort(basePort, ssl);
            }
        }

        return normalizePort(portInt, ssl);
    }

    @Override
    public String getScheme() {
        final String schemeStr = ForestVariable.getStringValue(scheme, request);
        if (StringUtils.isNotEmpty(schemeStr)) {
            return normalizeScheme(schemeStr);
        }
        checkAndRefreshURL();
        final String renderedScheme = ForestVariable.getStringValue(renderedURL.scheme, request);
        if (StringUtils.isNotEmpty(renderedScheme)) {
            return normalizeScheme(renderedScheme);
        }
        if (address != null) {
            final String addressScheme = address.getScheme();
            if (StringUtils.isNotEmpty(addressScheme)) {
                return normalizeScheme(addressScheme);
            }
        }
        if (baseURL != null) {
            final String baseScheme = baseURL.getScheme();
            if (StringUtils.isNotEmpty(baseScheme)) {
                return normalizeScheme(baseScheme);
            }
        }
        if (baseAddress != null) {
            final String baseAddressScheme = baseAddress.getScheme();
            if (StringUtils.isNotEmpty(baseAddressScheme)) {
                return normalizeScheme(baseAddressScheme);
            }
        }
        return normalizeScheme(renderedScheme);
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
        if (StringUtils.isNotEmpty(basePath)) {
            return basePath;
        }
        checkAndRefreshURL();
        final String renderedBasePath = renderedURL.getBasePath();
        if (StringUtils.isNotEmpty(renderedBasePath)) {
            return renderedBasePath;
        }
        if (address != null) {
            final String addressBasePath = address.getBasePath();
            if (StringUtils.isNotEmpty(addressBasePath)) {
                return addressBasePath;
            }
        }
        if (baseAddress != null) {
            final String baseAddressBasePath = baseAddress.getBasePath();
            if (StringUtils.isNotEmpty(baseAddressBasePath)) {
                return baseAddressBasePath;
            }
        }
        return renderedBasePath;
    }


    @Override
    public String getRef() {
        String refStr = ForestVariable.getStringValue(ref, request);
        if (StringUtils.isNotEmpty(refStr)) {
            return refStr;
        }
        checkAndRefreshURL();
        final String renderedRef = renderedURL.getRef();
        if (StringUtils.isNotEmpty(renderedRef)) {
            return renderedRef;
        }
        if (baseURL != null) {
            final String baseRef = baseURL.getRef();
            if (StringUtils.isNotEmpty(baseRef)) {
                return baseRef;
            }
        }
        return refStr;
    }

    @Override
    public void clear() {
        this.renderedURL = null;
    }

    @Override
    public void onChanged(MappingTemplate template, Object newValue) {

    }
}
