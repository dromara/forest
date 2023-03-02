package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestProperties;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.http.ForestQueryMap;
import com.dtflys.forest.http.SimpleQueryParameter;
import com.dtflys.forest.http.ForestURL;
import com.dtflys.forest.http.ForestURLBuilder;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

import java.lang.annotation.Annotation;

public class MappingURLTemplate extends MappingTemplate {


    public MappingURLTemplate(Class<? extends Annotation> annotationType, String attributeName, ForestMethod<?> forestMethod, String template, VariableScope variableScope, ForestProperties properties, MappingParameter[] parameters) {
        super(annotationType, attributeName, forestMethod, template, variableScope, properties, parameters);
    }

    @Override
    public String render(Object[] args) {
        return super.render(args);
    }

    public ForestURL render(Object[] args, ForestQueryMap queries) {
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
        try {
            ForestJsonConverter jsonConverter = variableScope.getConfiguration().getJsonConverter();
            int len = exprList.size();
            StringBuilder builder = new StringBuilder();
            SimpleQueryParameter lastQuery  = null;
            for (int i = 0; i < len; i++) {
                MappingExpr expr = exprList.get(i);
                String exprVal = String.valueOf(renderExpression(jsonConverter, expr, args));
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
                            userInfo.append(subBuilder.toString());
                            subBuilder = new StringBuilder();
                            continue;
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
                                continue;
                            } else if (scheme != null && host == null) {
                                // 解析地址部分
                                host = subBuilder.toString();
                                subBuilder = new StringBuilder();
                                if (ch == '/') {
                                    pathCharIndex--;
                                    renderedPath = true;
                                }
                                continue;
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
                if (path == null) {
                    path = new StringBuilder();
                }
                path.append(builder);
            }
            if (StringUtils.isNotEmpty(path)) {
                String[] group = path.toString().split("#", 2);
                if (group.length > 1) {
                    path = new StringBuilder();
                    path.append(group[0]);
                    ref = group[1];
                }
            }
            return new ForestURLBuilder()
                    .setScheme(scheme)
                    .setUserInfo(userInfo != null ? userInfo.toString() : null)
                    .setHost(host)
                    .setPort(port != null ? port : (host != null ? -1 : null))
                    .setPath(path != null ? path.toString() : null)
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
