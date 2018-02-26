package org.forest.backend.httpclient.handler;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.forest.backend.httpclient.response.HttpclientForestResponse;
import org.forest.converter.ForestConverter;
import org.forest.exceptions.ForestHandlerException;
import org.forest.handler.ResultHandler;
import org.forest.utils.ForestDataType;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

import java.io.*;
import java.lang.reflect.Type;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2016-05-25
 */
public class DefaultHttpclientResultHandler extends ResultHandler {

    public Object getResult(ForestRequest request, ForestResponse response, Type resultType, Class resultClass) {
        Object result = response.getResult();
        if (result != null && resultClass.isAssignableFrom(result.getClass())) {
            return result;
        }
        HttpclientForestResponse httpclientForestResponse = (HttpclientForestResponse) response;
        HttpEntity entity = httpclientForestResponse.getHttpResponse().getEntity();
        if (entity != null) {
            try {
                if (void.class.isAssignableFrom(resultClass)) {
                    return null;
                }
                if (ForestResponse.class.isAssignableFrom(resultClass)) {
                    return response;
                }
                if (boolean.class.isAssignableFrom(resultClass) || Boolean.class.isAssignableFrom(resultClass)) {
                    return response.isSuccess();
                }
                if (resultClass.isArray()) {
                    if (byte[].class.isAssignableFrom(resultClass)) {
                        return EntityUtils.toByteArray(entity);
                    }
                }
                String responseText = null;
                if (result != null && CharSequence.class.isAssignableFrom(result.getClass())) {
                    responseText = result.toString();
                }
                else {
                    responseText = response.getContent();
                }
                response.setContent(responseText);
                if (CharSequence.class.isAssignableFrom(resultClass)) {
                    return responseText;
                }
                if (InputStream.class.isAssignableFrom(resultClass)) {
                    return entity.getContent();
                }

                ForestDataType dataType = request.getDataType();
                if (dataType.equals(ForestDataType.TEXT)) {
                    return responseText;
                }
                ForestConverter converter = request.getConfiguration().getConverter(dataType);
                return converter.convertToJavaObject(responseText, resultType);

            } catch (IOException e) {
                throw new ForestHandlerException(e, request, response);
            }
        }
        else if (ForestResponse.class.isAssignableFrom(resultClass)) {
            return response;
        }
        return null;
    }
}
