package com.dtflys.forest.backend;

import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;

import java.util.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 18:06
 */
public abstract class AbstractBodyBuilder<T> implements BodyBuilder<T> {

    @Override
    public void buildBody(T httpRequest, ForestRequest request) {
        String contentType = request.getContentType();

        String[] typeGroup = contentType.split(";");
        String mineType = typeGroup[0];
        String charset = "UTF-8";
        String requestBody = request.getRequestBody();

        if (StringUtils.isEmpty(mineType)) {
            mineType = "application/x-www-form-urlencoded";
        }
        if (typeGroup.length > 1) {
            charset = typeGroup[1];
        }

        List<RequestNameValue> nameValueList = request.getDataNameValueList();
        if (requestBody != null) {
            setStringBody(httpRequest, requestBody, charset, contentType);
            return;
        }

        if (mineType.equals("application/x-www-form-urlencoded")) {
            setFormData(httpRequest, request, charset, contentType, nameValueList);
        }
        else if (mineType.equals("application/json")) {
            ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
            String text = null;
            Map<String, Object> map = convertNameValueListToMap(request, nameValueList);
            String json = jsonConverter.convertToJson(map);
            text = json;
            setStringBody(httpRequest, text, charset, contentType);
        }
        else  {
            Map<String, Object> map = convertNameValueListToMap(request, nameValueList);
            StringBuilder builder = new StringBuilder();
            for (Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Object> entry = iterator.next();
                Object value = entry.getValue();
                builder.append(value);
            }
            setStringBody(httpRequest, builder.toString(), charset, contentType);
        }
    }

    protected abstract void setStringBody(T httpReq, String text, String charset, String contentType);

    protected abstract void setFormData(T httpReq, ForestRequest request, String charset, String contentType, List<RequestNameValue> nameValueList);

    private Map<String, Object> convertNameValueListToMap(ForestRequest request, List<RequestNameValue> nameValueList) {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            if (value instanceof Date) {
                value = MappingTemplate.getParameterValue(jsonConverter, value);
            }
            if (value == null && StringUtils.isNotEmpty(name)) {
                Map nameMap = jsonConverter.convertToJavaObject(name, Map.class);
                if (nameMap != null && nameMap.size() > 0) {
                    map.putAll(nameMap);
                } else {
                    map.put(name, value);
                }
            }
            else {
                map.put(name, value);
            }
        }
        return map;
    }
}
