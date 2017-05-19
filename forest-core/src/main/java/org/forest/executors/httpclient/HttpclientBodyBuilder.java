package org.forest.executors.httpclient;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.forest.converter.json.ForestJsonConverter;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.executors.BodyBuilder;
import org.forest.http.ForestRequest;
import org.forest.mapping.MappingTemplate;
import org.forest.utils.RequestNameValue;
import org.forest.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:52
 */
public class HttpclientBodyBuilder<T extends HttpEntityEnclosingRequestBase> implements BodyBuilder<T> {

    @Override
    public void buildBody(T httpRequest, ForestRequest request) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        String contentType = request.getContentType();

        String[] typeGroup = contentType.split(";");
        String mineType = typeGroup[0];
        String charset = HTTP.UTF_8;
        if (StringUtils.isEmpty(mineType)) {
            mineType = "application/x-www-form-urlencoded";
        }
        if (typeGroup.length > 1) {
            charset = typeGroup[1];
        }

        List<RequestNameValue> nameValueList = request.getDataNameValueList();

        if (mineType.equals("application/x-www-form-urlencoded")) {
            setEntities(httpRequest, request, contentType, nameValueList, nameValuePairs);
        }
        else if (mineType.equals("application/json")) {
            ForestJsonConverter jsonConverter = request.getConfiguration().getJsonCoverter();
            String text = null;
            if (request.getRequestBody() != null) {
                text = request.getRequestBody();
            }
            else {
                Map<String, Object> map = convertNameValueListToJSON(request, nameValueList);
                String json = jsonConverter.convertToJson(map);
                text = json;
            }
            try {
                StringEntity entity = new StringEntity(text, charset);
                entity.setContentType(contentType);
//                        entity.setContentEncoding(HTTP.UTF_8);
                if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
                    ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(entity);
                }
            } catch (UnsupportedEncodingException e) {
                throw new ForestRuntimeException(e);
            }
        }

    }


    private void setEntities(T httpReq, ForestRequest request, String contentType, List<RequestNameValue> nameValueList, List<NameValuePair> nameValuePairs) {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonCoverter();
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            NameValuePair nameValuePair = new BasicNameValuePair(name, MappingTemplate.getParameterValue(jsonConverter, value));
            nameValuePairs.add(nameValuePair);
        }

        try {
            StringEntity entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
            if (StringUtils.isNotEmpty(contentType)) {
                entity.setContentType(contentType);
            }
            if (httpReq instanceof HttpEntityEnclosingRequestBase) {
                ((HttpEntityEnclosingRequestBase) httpReq).setEntity(entity);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> convertNameValueListToJSON(ForestRequest request, List<RequestNameValue> nameValueList) {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonCoverter();
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
