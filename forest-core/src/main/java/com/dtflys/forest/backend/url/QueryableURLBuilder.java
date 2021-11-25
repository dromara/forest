package com.dtflys.forest.backend.url;

import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.http.ForestQueryParameter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 带查询参数的URL构造器
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:11
 */
public class QueryableURLBuilder extends URLBuilder {

    @Override
    public String buildUrl(ForestRequest request) {
        String url = request.getUrl();
        List<ForestQueryParameter> queryParameters = request.getQueryValues();
        StringBuilder paramBuilder = new StringBuilder();
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        for (int i = 0; i < queryParameters.size(); i++) {
            ForestQueryParameter queryParam = queryParameters.get(i);
            String name = queryParam.getName();
            if (name != null) {
                paramBuilder.append(queryParam.getName());
            }
            String value = MappingTemplate.getParameterValue(jsonConverter, queryParam.getValue());
            if (StringUtils.isNotEmpty(value) && request.getCharset() != null) {
                if (name != null) {
                    paramBuilder.append('=');
                }
                String charset = queryParam.getCharset();
                if (StringUtils.isBlank(charset)) {
                    charset = request.getCharset();
                }
                if (StringUtils.isBlank(charset)) {
                    charset = "UTF-8";
                }
                String encodedValue = null;
                if (queryParam.isUrlencoded()) {
                    encodedValue = URLUtils.allEncode(value, charset);
                } else {
                    encodedValue = URLUtils.queryValueEncode(value, charset);
                }
                if (encodedValue != null) {
                    paramBuilder.append(encodedValue);
                }
            }
            if (i < queryParameters.size() - 1) {
                paramBuilder.append('&');
            }
        }
        StringBuilder urlBuilder = new StringBuilder(url);
        String query = paramBuilder.toString();
        if (StringUtils.isNotEmpty(query)) {
            urlBuilder.append("?").append(query);
        }
        String ref = request.getRef();
        if (StringUtils.isNotEmpty(ref)) {
            urlBuilder.append("#").append(ref);
        }
        return urlBuilder.toString();
    }

}
