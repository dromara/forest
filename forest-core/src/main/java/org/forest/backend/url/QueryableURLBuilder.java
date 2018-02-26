package org.forest.backend.url;

import org.forest.converter.json.ForestJsonConverter;
import org.forest.http.ForestRequest;
import org.forest.mapping.MappingTemplate;
import org.forest.utils.RequestNameValue;
import org.forest.utils.StringUtils;

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
        List<RequestNameValue> data = request.getDataNameValueList();
        StringBuilder paramBuilder = new StringBuilder();
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        for (int i = 0; i < data.size(); i++) {
            RequestNameValue nameValue = data.get(i);
            paramBuilder.append(nameValue.getName());
            String value = MappingTemplate.getParameterValue(jsonConverter, nameValue.getValue());
            paramBuilder.append('=');
            if (StringUtils.isNotEmpty(value)) {
                String encodedValue = null;
                try {
                    encodedValue = URLEncoder.encode(value, request.getEncode());
                } catch (UnsupportedEncodingException e) {
                }
                if (encodedValue != null) {
                    paramBuilder.append(encodedValue);
                }
            }
            if (i < data.size() - 1) {
                paramBuilder.append('&');
            }
        }
        String query = paramBuilder.toString();
        if (StringUtils.isNotEmpty(query)) {
            return url + "?" + query;
        }
        return url;
    }

}
