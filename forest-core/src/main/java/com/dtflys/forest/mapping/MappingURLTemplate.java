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

import java.net.MalformedURLException;

public class MappingURLTemplate extends MappingTemplate {

    private String schema;

    private String userInfo;

    private String host;

    private Integer port;

    private String path;

    private ForestQueryMap queries;

    public MappingURLTemplate(ForestMethod<?> forestMethod, String template, VariableScope variableScope, ForestProperties properties, MappingParameter[] parameters) {
        super(forestMethod, template, variableScope, properties, parameters);
    }

    @Override
    public String render(Object[] args) {
        boolean renderedQuery = false;
        queries = new ForestQueryMap();
        try {
            ForestJsonConverter jsonConverter = variableScope.getConfiguration().getJsonConverter();
            int len = exprList.size();
            StringBuilder builder = new StringBuilder();
            ForestQueryParameter lastQuery  = null;
            for (int i = 0; i < len; i++) {
                MappingExpr expr = exprList.get(i);
                String val = renderExpression(jsonConverter, expr, args);
                if (val != null) {
                    builder.append(val);
                }
                if (renderedQuery) {
                    // 已渲染到查询参数
                    if (lastQuery != null && (
                            expr instanceof MappingReference
                            || expr instanceof MappingUrlEncodedExpr
                            || expr instanceof MappingProperty
                            || expr instanceof MappingInvoke
                            || expr instanceof MappingIndex)) {
                        // 在查询参数的位置进行变量引用
                        Object lastQueryValue = lastQuery.getValue();
                        String queryVal = lastQueryValue == null ? val : lastQueryValue + val;
                        lastQuery.setValue(queryVal);
                    } else {
                        // 非变量引用
                        String[] subQueries = val.split("&");
                        int subQueryLen = subQueries.length;
                        int k = 1;
                        if (val.charAt(0) != '&') {
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
                    // 未渲染到查询参数
                    int queryIndex = val.indexOf('?');
                    renderedQuery = queryIndex >= 0;

                    String prefix = builder.toString();
                    String baseUrl = prefix.substring(0, queryIndex > 0 ? queryIndex : prefix.length());
                    char[] baseUrlChars = baseUrl.toCharArray();
                    int baseLen = baseUrlChars.length;
                    char ch;
                    StringBuilder subBuilder = new StringBuilder();
                    for (int k = 0; k < baseLen; k++) {
                        ch = baseUrlChars[k];
                        if (ch == ':') {
                            if (schema == null && k + 1 < baseLen
                                    && baseUrlChars[k + 1] == '/') {
                                schema = subBuilder.toString();
                                subBuilder = new StringBuilder();
                                k++;
                                continue;
                            } else if (host == null && port == null && k + 1 < baseLen
                                    && Character.isDigit(baseUrlChars[k + 1])) {
                                host = subBuilder.toString();
                                subBuilder = new StringBuilder();
                                k++;
                                while (k < baseLen) {
                                    ch = baseUrlChars[k];
                                    if (Character.isDigit(ch)) {
                                        subBuilder.append(ch);
                                    } else {
                                        throw new ForestRuntimeException("URL Path '" + baseUrl + "' can not be parsed");
                                    }
                                }
                                port = Integer.parseInt(subBuilder.toString());
                            }
                        }
                        subBuilder.append(ch);
                    }

                    if (renderedQuery) {
                        if (queryIndex + 1 < prefix.length()) {
                            String queryStr = prefix.substring(queryIndex + 1);
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
            return builder.toString();
        } catch (ForestVariableUndefinedException ex) {
            throw new ForestVariableUndefinedException(ex.getVariableName(), template);
        }
    }
}
