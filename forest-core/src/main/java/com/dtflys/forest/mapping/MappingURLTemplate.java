package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestProperties;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.http.ForestQueryMap;
import com.dtflys.forest.http.ForestQueryParameter;
import com.dtflys.forest.http.ForestURL;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class MappingURLTemplate extends MappingTemplate {


    public MappingURLTemplate(Class<? extends Annotation> annotationType, String attributeName, ForestMethod<?> forestMethod, String template, VariableScope variableScope, ForestProperties properties, MappingParameter[] parameters) {
        super(annotationType, attributeName, forestMethod, template, variableScope, properties, parameters);
    }

    @Override
    public String render(Object[] args) {
        return super.render(args);
    }

    public ForestURL render(Object[] args, ForestQueryMap queries) {
        String schema = null;
        String userInfo = null;
        String host = null;
        Integer port = null;
        String path = null;
        StringBuilder urlBuilder = new StringBuilder();

        boolean renderedQuery = false;
        boolean nextIsPort = false;
        boolean renderedPath = false;
        try {
            ForestJsonConverter jsonConverter = variableScope.getConfiguration().getJsonConverter();
            int len = exprList.size();
            StringBuilder builder = new StringBuilder();
            ForestQueryParameter lastQuery  = null;
            for (int i = 0; i < len; i++) {
                MappingExpr expr = exprList.get(i);
                String exprVal = renderExpression(jsonConverter, expr, args);
                if (exprVal != null) {
                    builder.append(exprVal);
                }
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
                                    lastQuery = new ForestQueryParameter(lastQueryPartVal);
                                } else {
                                    lastQuery = new ForestQueryParameter(keyValue[0], keyValue[1]);
                                }
                                queries.addQuery(lastQuery);
                            }
                        }
                        // 解析查询参数
                        for ( ; k < subQueryLen; k++) {
                            String queryItem = subQueries[k];
                            String[] keyValue = queryItem.split("=", 2);
                            if (keyValue.length == 1) {
                                lastQuery = new ForestQueryParameter(queryItem);
                            } else {
                                lastQuery = new ForestQueryParameter(keyValue[0]);
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
                    int queryIndex = exprVal.indexOf('?');
                    renderedQuery = queryIndex >= 0;

                    String baseUrl = exprVal;
                    if (renderedQuery) {
                        baseUrl = exprVal.substring(0, queryIndex);
                        urlBuilder.append(baseUrl);
                    } else {
                        urlBuilder.append(baseUrl);
                    }
                    char[] baseUrlChars = baseUrl.toCharArray();
                    int baseLen = baseUrlChars.length;
                    char ch;
                    StringBuilder subBuilder = new StringBuilder();
                    for (int pathCharIndex = 0 ; pathCharIndex < baseLen; pathCharIndex++) {
                        ch = baseUrlChars[pathCharIndex];
                        if (ch == ':') {
                            if (schema == null && pathCharIndex + 1 < baseLen
                                    && baseUrlChars[pathCharIndex + 1] == '/') {
                                // 解析协议部分
                                schema = subBuilder.toString();
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
                            if (schema != null && host == null) {
                                // 解析地址部分
                                boolean hasNext = pathCharIndex + 1 < baseLen;
                                if (!hasNext || (hasNext && Character.isDigit(baseUrlChars[pathCharIndex + 1]))) {
                                    host = subBuilder.toString();
                                    subBuilder = new StringBuilder();
                                    nextIsPort = true;
                                    continue;
                                } else if (hasNext && !Character.isDigit(baseUrlChars[pathCharIndex + 1])) {
                                    if (userInfo == null) {
                                        userInfo = subBuilder.toString() + ':';
                                    } else {
                                        userInfo += subBuilder.toString() + ':';
                                    }
                                    subBuilder = new StringBuilder();
                                    continue;
                                }
                            } else if (host != null && port == null && !renderedPath) {
                                nextIsPort = true;
                            } else {
                                subBuilder.append(ch);
                            }
                        } else if (ch == '@') {
                            // 解析用户名密码
                            if (userInfo == null) {
                                if (host != null) {
                                    StringBuilder userInfoBuilder = new StringBuilder(host);
                                    if (StringUtils.isNotEmpty(path)) {
                                        userInfoBuilder.append(path);
                                        path = "";
                                    }
                                    if (subBuilder.length() > 0) {
                                        if (!renderedPath) {
                                            userInfoBuilder.append(':');
                                        }
                                        userInfoBuilder.append(subBuilder);
                                        subBuilder = new StringBuilder();
                                    }
                                    userInfo = userInfoBuilder.toString();
                                    host = null;
                                } else {
                                    userInfo = "";
                                }
                                if (port != null) {
                                    userInfo += port;
                                    port = null;
                                }
                            } else {
                                userInfo += subBuilder.toString();
                                subBuilder = new StringBuilder();
                            }
                            renderedPath = false;
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
                            } else if (schema != null && host == null) {
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
                                if (pathCharIndex + 1 == baseLen) {
                                    if (path == null) {
                                        path = subBuilder.toString();
                                    } else {
                                        path += subBuilder.toString();
                                    }
                                }
                            }
                        } else {
                            subBuilder.append(ch);
                        }
                    }

                    if (renderedQuery) {
                        if (queryIndex + 1 < exprVal.length()) {
                            String queryStr = exprVal.substring(queryIndex + 1);
                            String[] queryItems = queryStr.split("&");
                            if (queryItems.length > 0) {
                                for (String queryItem : queryItems) {
                                    String[] keyValue = queryItem.split("=", 2);
                                    lastQuery = new ForestQueryParameter(keyValue[0]);
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
            return new ForestURL(schema, userInfo, host, port, path);
        } catch (ForestVariableUndefinedException ex) {
            throw new ForestVariableUndefinedException(annotationType, attributeName, forestMethod, ex.getVariableName(), template);
        }
    }

    public ForestURL getURL() {
        return null;
    }
}
