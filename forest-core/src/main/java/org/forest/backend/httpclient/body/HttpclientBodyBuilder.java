package org.forest.backend.httpclient.body;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.forest.backend.AbstractBodyBuilder;
import org.forest.converter.json.ForestJsonConverter;
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
public class HttpclientBodyBuilder<T extends HttpEntityEnclosingRequestBase> extends AbstractBodyBuilder<T> {




    protected void setStringBody(T httpReq, String text, String charset, String contentType) {
            StringEntity entity = new StringEntity(text, charset);
            entity.setContentType(contentType);
            httpReq.setEntity(entity);
    }

    protected void setFormData(T httpReq, ForestRequest request, String charset, String contentType, List<RequestNameValue> nameValueList) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            if (nameValue.isInQuery()) continue;
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            NameValuePair nameValuePair = new BasicNameValuePair(name, MappingTemplate.getParameterValue(jsonConverter, value));
            nameValuePairs.add(nameValuePair);
        }

        try {
            StringEntity entity = new UrlEncodedFormEntity(nameValuePairs, charset);
            if (StringUtils.isNotEmpty(contentType)) {
                entity.setContentType(contentType);
            }
            httpReq.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



}
