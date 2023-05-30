package org.dromara.forest.mapping;

import org.dromara.forest.config.ForestProperties;
import org.dromara.forest.config.VariableScope;
import org.dromara.forest.converter.json.ForestJsonConverter;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.exceptions.ForestVariableUndefinedException;
import org.dromara.forest.http.ForestQueryMap;
import org.dromara.forest.http.SimpleQueryParameter;
import org.dromara.forest.http.ForestURL;
import org.dromara.forest.http.ForestURLBuilder;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.utils.StringUtils;

import java.lang.annotation.Annotation;

public class MappingURLTemplate extends MappingTemplate {


    public MappingURLTemplate(Class<? extends Annotation> annotationType, String attributeName, ForestMethod<?> forestMethod, String template, VariableScope variableScope, ForestProperties properties, MappingParameter[] parameters) {
        super(annotationType, attributeName, forestMethod, template, variableScope, properties, parameters);
    }

    @Override
    public String render(final Object[] args) {
        return super.render(args);
    }

    public ForestURL render(final Object[] args, final ForestQueryMap queries) {
        String scheme = null;
        StringBuilder userInfo = null;
        String host = null;
        Integer port = null;
        final StringBuilder path = new StringBuilder();
        String ref = null;
        final StringBuilder urlBuilder = new StringBuilder();

        boolean renderedQuery = false;
        boolean nextIsPort = false;
        boolean renderedPath = false;
        try {
            final ForestJsonConverter jsonConverter = variableScope.getConfiguration().getJsonConverter();
            final int len = exprList.size();
            final StringBuilder builder = new StringBuilder();
            SimpleQueryParameter lastQuery  = null;
            for (int i = 0; i < len; i++) {
                final MappingExpr expr = exprList.get(i);
                String exprVal = String.valueOf(renderExpression(jsonConverter, expr, args));
                builder.append(exprVal);
                if (renderedQuery) {
                    // 已渲染到查询参数
                    if (lastQuery != null && (
                            expr instanceof MappingUrlEncodedExpr)) {
                        // 在查询参数的位置进行变量引用
                        final Object lastQueryValue = lastQuery.getValue();
                        final String queryVal = lastQueryValue == null ? exprVal : lastQueryValue + exprVal;
                        lastQuery.setValue(queryVal);
                    } else {
                        // 非变量引用
                        final String[] subQueries = exprVal.split("&");
                        final int subQueryLen = subQueries.length;
                        int k = 1;
                        if (exprVal.charAt(0) != '&') {
                            // 非连接符 & 开头
                            final String lastQueryPartVal = subQueries[0];
                            if (lastQuery != null) {
                                // 可能要接着上一个查询参数
                                final Object lastQueryValue = lastQuery.getValue();
                                final String queryVal = lastQueryValue == null ? lastQueryPartVal : lastQueryValue + lastQueryPartVal;
                                lastQuery.setValue(queryVal);
                            } else {
                                // 可能是第一个查询参数
                                final String[] keyValue = lastQueryPartVal.split("=", 2);
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
                            final String queryItem = subQueries[k];
                            final String[] keyValue = queryItem.split("=", 2);
                            if (keyValue.length == 1) {
                                lastQuery = new SimpleQueryParameter(queries, queryItem);
                            } else {
                                lastQuery = new SimpleQueryParameter(queries, keyValue[0]);
                                final String queryVal = keyValue[1];
                                if (StringUtils.isNotBlank(queryVal)) {
                                    lastQuery.setValue(queryVal);
                                }
                            }
                            queries.addQuery(lastQuery);
                        }
                    }
                } else {
                    // 查询参数前面部分
                    final int refIndex = exprVal.indexOf('#');
                    final int queryIndex = exprVal.indexOf('?');
                    renderedQuery = ref == null && queryIndex >= 0 && (queryIndex < refIndex || refIndex < 0);

                    String baseUrl = exprVal;
                    if (renderedQuery) {
                        baseUrl = exprVal.substring(0, queryIndex);
                    } else if (host != null && !nextIsPort && port == null && path == null) {
                        baseUrl = host + baseUrl;
                        host = null;
                        urlBuilder.setLength(0);
                        urlBuilder.append("//");
                    }
                    urlBuilder.append(baseUrl);
                    final char[] baseUrlChars = baseUrl.toCharArray();
                    final int baseLen = baseUrlChars.length;
                    final StringBuilder subBuilder = new StringBuilder();
                    for (int pathCharIndex = 0 ; pathCharIndex < baseLen; pathCharIndex++) {
                        char ch = baseUrlChars[pathCharIndex];
                        if (!renderedPath && ch == ':') {
                            if (scheme == null && pathCharIndex + 1 < baseLen
                                    && baseUrlChars[pathCharIndex + 1] == '/') {
                                // 解析协议部分
                                scheme = subBuilder.toString();
                                subBuilder.setLength(0);
                                pathCharIndex++;
                                ch = baseUrlChars[pathCharIndex];
                                if (ch != '/') {
                                    throw new ForestRuntimeException("URI '" + super.render(args) + "' is invalid.");
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
                                final boolean hasNext = pathCharIndex + 1 < baseLen;
                                if (!hasNext || (hasNext && Character.isDigit(baseUrlChars[pathCharIndex + 1]))) {
                                    host = subBuilder.toString();
                                    subBuilder.setLength(0);
                                    nextIsPort = true;
                                } else if (hasNext && !Character.isDigit(baseUrlChars[pathCharIndex + 1])) {
                                    if (userInfo == null) {
                                        userInfo = new StringBuilder(subBuilder.toString() + ':');
                                    } else {
                                        userInfo.append(subBuilder).append(':');
                                    }
                                    subBuilder.setLength(0);
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
                            subBuilder.setLength(0);
                        } else if (ch == '/' || pathCharIndex + 1 == baseLen) {
                            if (ch != '/') {
                                subBuilder.append(ch);
                            }
                            if (!renderedPath && nextIsPort && port == null) {
                                // 解析端口号
                                port = Integer.parseInt(subBuilder.toString());
                                subBuilder.setLength(0);
                                nextIsPort = false;
                                if (ch == '/') {
                                    pathCharIndex--;
                                    renderedPath = true;
                                }
                            } else if (scheme != null && host == null) {
                                // 解析地址部分
                                host = subBuilder.toString();
                                subBuilder.setLength(0);
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
                                    path.append(subBuilder);
                                    subBuilder.setLength(0);
                                }
                            }
                        } else {
                            subBuilder.append(ch);
                        }
                    }

                    if (refIndex > queryIndex) {
                        final String[] group = exprVal.split("#", 2);
                        exprVal = group[0];
                        ref = group[1];
                    }
                    if (renderedQuery) {
                        if (queryIndex + 1 < exprVal.length()) {
                            final String queryStr = exprVal.substring(queryIndex + 1);
                            final String[] queryItems = queryStr.split("&");
                            for (String queryItem : queryItems) {
                                final String[] keyValue = queryItem.split("=", 2);
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
            if (host == null && StringUtils.isEmpty(path) && StringUtils.isNotEmpty(urlBuilder)) {
                if (scheme == null) {
                    final String urlStr = urlBuilder.toString();
                    if (!urlStr.startsWith("/")) {
                        final String[] urlStrGroup = urlStr.split(":");
                        if (urlStrGroup.length > 1) {
                            final String urlGroup0 = urlStrGroup[0];
                            final String urlGroup1 = urlStrGroup[1];
                            try {
                                port = Integer.valueOf(urlGroup1);
                                host = urlGroup0;
                            } catch (Throwable th) {
                                path.append(builder);
                            }
                        } else {
                            final String urlGroup0 = urlStrGroup[0];
                            if (urlGroup0.equals("localhost") ||
                                    urlGroup0.matches("^(((\\d)|([1-9]\\d)|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d)|([1-9]\\d)|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))$")) {
                                host = urlGroup0;
                            }
                        }
                    }
                } else {
                    path.append(builder);
                }
            }
            if (StringUtils.isNotEmpty(path)) {
                final String[] group = path.toString().split("#", 2);
                if (group.length > 1) {
                    path.setLength(0);
                    path.append(group[0]);
                    ref = group[1];
                }
            }
            return new ForestURLBuilder()
                    .setScheme(scheme)
                    .setUserInfo(userInfo != null ? userInfo.toString() : null)
                    .setHost(host)
                    .setPort(port != null ? port : (host != null ? -1 : null))
                    .setPath(path.length() > 0 ? path.toString() : null)
                    .setRef(ref)
                    .build();
        } catch (ForestVariableUndefinedException ex) {
            throw new ForestVariableUndefinedException(annotationType, attributeName, forestMethod, ex.getVariableName(), template);
        }
    }

    public ForestURL getURL() {
        return null;
    }
}
