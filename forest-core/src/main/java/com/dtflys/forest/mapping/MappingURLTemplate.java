package com.dtflys.forest.mapping;

import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.ForestProperties;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.*;
import com.dtflys.forest.http.*;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ForestCache;
import com.dtflys.forest.utils.StringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class MappingURLTemplate extends MappingTemplate {

    private final static ForestCache<String, MappingURLTemplate> URL_TEMPLATE_CACHE = new ForestCache<>(256);

    private final static int MAX_CACHE_SIZE = 128;

    public static MappingURLTemplate get(final String text) {
        return get(Forest.config(), text);
    }

    public static MappingURLTemplate get(
            final ForestConfiguration configuration,
            final String text) {
        return get(null, configuration, null, text);
    }

    public static MappingURLTemplate get(
            final Class<? extends Annotation> annotationType,
            final ForestConfiguration configuration,
            final String attributeName,
            final String text) {
        return get(annotationType, null, configuration, attributeName, text);
    }

    public static MappingURLTemplate get(
            final Class<? extends Annotation> annotationType,
            final ForestMethod method,
            final ForestConfiguration configuration,
            final String attributeName,
            final String text) {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalArgumentException("url text cannot be empty");
        }
        if (text.length() <= MAX_CACHE_SIZE) {
            return URL_TEMPLATE_CACHE.get(text, () ->
                    create(annotationType, method, configuration, attributeName, text));
        }
        return create(annotationType, method, configuration, attributeName, text);
    }

    public static MappingURLTemplate create(
            final Class<? extends Annotation> annotationType,
            final ForestMethod method,
            final ForestConfiguration configuration,
            final String attributeName,
            final String text) {
        return new MappingURLTemplate(
                URL,
                annotationType,
                attributeName,
                method,
                text,
                configuration.getProperties(),
                method == null ? null : method.getForestParameters());
    }


    public MappingURLTemplate(int type, Class<? extends Annotation> annotationType, String attributeName, ForestMethod<?> forestMethod, String template, ForestProperties properties, MappingParameter[] parameters) {
        super(type, annotationType, attributeName, forestMethod, template, parameters);
    }

    @Override
    public String render(VariableScope scope, Object[] args) {
        return super.render(scope, args);
    }

    public ForestURL render(VariableScope scope, Object[] args, ForestQueryMap queries) {
        return render(isConstant ? ForestURL.emptyURL() : new ForestDynamicURL(this), scope, args, queries);
    }

    public ForestURL render(ForestURL url, VariableScope scope, Object[] args, ForestQueryMap queries) {
        return render(url, scope, args, queries, true);
    }

    public ForestURL render(ForestURL url, VariableScope scope, Object[] args, ForestQueryMap queries, boolean allowEmptyBrace) {
        String scheme = null;
        StringBuilder userInfo = null;
        String host = null;
        Integer port = null;
        StringBuilder path = null;
        String ref = null;
        StringBuilder urlBuilder = new StringBuilder();

        boolean renderedQuery = false;
        boolean nextIsPort = false;
        boolean renderedPath = false;
        ForestJsonConverter jsonConverter = scope.getConfiguration().getJsonConverter();
        int len = exprList.size();
        StringBuilder builder = new StringBuilder();
        SimpleQueryParameter lastQuery  = null;
        for (int i = 0; i < len; i++) {
            MappingExpr expr = exprList.get(i);
            String exprVal = String.valueOf(renderExpression(scope, jsonConverter, expr, args, allowEmptyBrace, !renderedQuery));
            builder.append(exprVal);
            if (renderedQuery) {
                // 已渲染到查询参数
                if (lastQuery != null && (
                        expr instanceof MappingUrlEncodedExpr)) {
                    // 在查询参数的位置进行变量引用
                    Object lastQueryValue = lastQuery.getValue();
                    String queryVal = lastQueryValue == null ? exprVal : lastQueryValue + exprVal;
                    lastQuery.setValue(queryVal);
                } else {
                    // 非变量引用
                    String[] subQueries = exprVal.split("&");
                    int subQueryLen = subQueries.length;
                    int k = 1;
                    if (exprVal.charAt(0) != '&') {
                        // 非连接符 & 开头
                        String lastQueryPartVal = subQueries[0];
                        if (lastQuery != null) {
                            // 可能要接着上一个查询参数
                            Object lastQueryValue = lastQuery.getValue();
                            String queryVal = lastQueryValue == null ? lastQueryPartVal : lastQueryValue + lastQueryPartVal;
                            lastQuery.setValue(queryVal);
                        } else {
                            // 可能是第一个查询参数
                            String[] keyValue = lastQueryPartVal.split("=", 2);
                            if (keyValue.length == 1) {
                                lastQuery = new SimpleQueryParameter(queries, lastQueryPartVal);
                            } else {
                                lastQuery = new SimpleQueryParameter(queries, keyValue[0], keyValue[1]);
                            }
                            queries.addQuery(lastQuery);
                        }
                    }
                    // 解析查询参数
                    for ( ; k < subQueryLen; k++) {
                        String queryItem = subQueries[k];
                        String[] keyValue = queryItem.split("=", 2);
                        if (keyValue.length == 1) {
                            lastQuery = new SimpleQueryParameter(queries, queryItem);
                        } else {
                            lastQuery = new SimpleQueryParameter(queries, keyValue[0]);
                            String queryVal = keyValue[1];
                            if (StringUtils.isNotBlank(queryVal)) {
                                lastQuery.setValue(queryVal);
                            }
                        }
                        queries.addQuery(lastQuery);
                    }
                }
            } else {
                // 查询参数前面部分
                int refIndex = exprVal.indexOf('#');
                int queryIndex = exprVal.indexOf('?');
                renderedQuery = ref == null && queryIndex >= 0 && (queryIndex < refIndex || refIndex < 0);

                String baseUrl = exprVal;
                if (renderedQuery) {
                    baseUrl = exprVal.substring(0, queryIndex);
                } else if (host != null && !nextIsPort && port == null && path == null) {
                    baseUrl = host + baseUrl;
                    host = null;
                    urlBuilder = new StringBuilder(scheme).append("//");
                }
                urlBuilder.append(baseUrl);
                char[] baseUrlChars = baseUrl.toCharArray();
                int baseLen = baseUrlChars.length;
                char ch;
                StringBuilder subBuilder = new StringBuilder();
                for (int pathCharIndex = 0 ; pathCharIndex < baseLen; pathCharIndex++) {
                    ch = baseUrlChars[pathCharIndex];
                    if (!renderedPath && ch == ':') {
                        if (scheme == null && pathCharIndex + 1 < baseLen
                                && baseUrlChars[pathCharIndex + 1] == '/') {
                            // 解析协议部分
                            scheme = subBuilder.toString();
                            subBuilder = new StringBuilder();
                            pathCharIndex++;
                            ch = baseUrlChars[pathCharIndex];
                            if (ch != '/') {
                                throw new ForestRuntimeException("URI '" + super.render(scope, args) + "' is invalid.");
                            }
                            pathCharIndex++;
                            if (pathCharIndex + 1 < baseLen && baseUrlChars[pathCharIndex + 1] == '/') {
                                do {
                                    pathCharIndex++;
                                } while (pathCharIndex + 1 < baseLen && baseUrlChars[pathCharIndex + 1] == '/');
                            }
                            continue;
                        }
                        if (scheme != null && host == null) {
                            // 解析地址部分
                            boolean hasNext = pathCharIndex + 1 < baseLen;
                            if (!hasNext || (hasNext && Character.isDigit(baseUrlChars[pathCharIndex + 1]))) {
                                host = subBuilder.toString();
                                subBuilder = new StringBuilder();
                                nextIsPort = true;
                            } else if (hasNext && !Character.isDigit(baseUrlChars[pathCharIndex + 1])) {
                                if (userInfo == null) {
                                    userInfo = new StringBuilder(subBuilder.toString() + ':');
                                } else {
                                    userInfo.append(subBuilder).append(':');
                                }
                                subBuilder = new StringBuilder();
                            }
                        } else if (host != null && port == null) {
                            nextIsPort = true;
                        } else {
                            subBuilder.append(ch);
                        }
                    } else if (!renderedPath && ch == '@') {
                        // 解析用户名密码
                        if (userInfo == null) {
                            if (host != null) {
                                userInfo = new StringBuilder(host);
                                host = null;
                            } else {
                                userInfo = new StringBuilder();
                            }
                        }
                        if (nextIsPort) {
                            userInfo.append(':');
                        }
                        userInfo.append(subBuilder);
                        subBuilder = new StringBuilder();
                    } else if (ch == '/' || pathCharIndex + 1 == baseLen) {
                        if (ch != '/') {
                            subBuilder.append(ch);
                        }
                        if (!renderedPath && nextIsPort && port == null) {
                            // 解析端口号
                            port = Integer.parseInt(subBuilder.toString());
                            subBuilder = new StringBuilder();
                            nextIsPort = false;
                            if (ch == '/') {
                                pathCharIndex--;
                                renderedPath = true;
                            }
                        } else if (scheme != null && host == null) {
                            // 解析地址部分
                            host = subBuilder.toString();
                            subBuilder = new StringBuilder();
                            if (ch == '/') {
                                pathCharIndex--;
                                renderedPath = true;
                            }
                        } else {
                            if (ch == '/') {
                                subBuilder.append(ch);
                                renderedPath = true;
                            }
                            if (renderedPath) {
                                if (path == null) {
                                    path = new StringBuilder(subBuilder.toString());
                                } else {
                                    path.append(subBuilder.toString());
                                }
                                subBuilder = new StringBuilder();
                            }
                        }
                    } else {
                        subBuilder.append(ch);
                    }
                }
                if (refIndex > queryIndex) {
                    String[] group = exprVal.split("#", 2);
                    exprVal = group[0];
                    ref = group[1];
                }
                if (renderedQuery) {
                    if (queryIndex + 1 < exprVal.length()) {
                        String queryStr = exprVal.substring(queryIndex + 1);
                        String[] queryItems = queryStr.split("&");
                        if (queryItems.length > 0) {
                            for (String queryItem : queryItems) {
                                String[] keyValue = queryItem.split("=", 2);
                                lastQuery = new SimpleQueryParameter(queries, keyValue[0]);
                                queries.addQuery(lastQuery);
                                if (keyValue.length > 1 && StringUtils.isNotBlank(keyValue[1])) {
                                    lastQuery.setValue(keyValue[1]);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (host == null && StringUtils.isEmpty(path) && StringUtils.isNotEmpty(urlBuilder)) {
            if (scheme == null) {
                String urlStr = urlBuilder.toString();
                if (!urlStr.startsWith("/")) {
                    String[] urlStrGroup = urlStr.split(":");
                    if (urlStrGroup.length > 1) {
                        String urlGroup0 = urlStrGroup[0];
                        String urlGroup1 = urlStrGroup[1];
                        try {
                            port = Integer.valueOf(urlGroup1);
                            host = urlGroup0;
                        } catch (Throwable th) {
                            if (path == null) {
                                path = new StringBuilder();
                            }
                            path.append(builder);
                        }
                    } else {
                        String urlGroup0 = urlStrGroup[0];
                        if (urlGroup0.equals("localhost") ||
                                urlGroup0.matches("^(((\\d)|([1-9]\\d)|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d)|([1-9]\\d)|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))$")) {
                            host = urlGroup0;
                        } else {
                            if (path == null) {
                                path = new StringBuilder();
                            }
                            path.append(builder);
                        }
                    }
                }
            } else {
                if (path == null) {
                    path = new StringBuilder();
                }
                path.append(builder);
            }
        }
        if (StringUtils.isNotEmpty(path)) {
            String[] group = path.toString().split("#", 2);
            if (group.length > 1) {
                path = new StringBuilder();
                path.append(group[0]);
                ref = group[1];
            }
        }

        url.setScheme(scheme)
                .setUserInfo(userInfo != null ? userInfo.toString() : null)
                .setHost(host)
                .setPort(port != null ? port : (host != null ? -1 : null))
                .setPath(path != null ? path.toString() : null)
                .setRef(ref);
        if (listener != null) {
            listener.onChanged(this, url);
        }
        return url;
    }

    @Override
    protected Object postValueRender(Object value, boolean enable) {
        if (value == null || !enable) {
            return value;
        }
        if (value instanceof Collection) {
            Collection collection = (Collection) value;
            int len = collection.size();
            int index = RandomUtils.nextInt(0, len);
            return collection.toArray()[index];
        }
        if (value.getClass().isArray()) {
            int len = Array.getLength(value);
            int index = RandomUtils.nextInt(0, len);
            return Array.get(value, index);
        }
        return super.postValueRender(value, enable);
    }
}
