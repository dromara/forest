package com.dtflys.forest.backend.url;

import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.http.ForestQueryParameter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
                if (queryParam.isUrlencoded()) {
                    String encodedValue = null;
                    try {
                        encodedValue = URLEncoder.encode(value, request.getCharset());
                    } catch (UnsupportedEncodingException e) {
                    }
                    if (encodedValue != null) {
                        paramBuilder.append(encodedValue);
                    }
                } else {
                    paramBuilder.append(value);
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
