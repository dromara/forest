package com.dtflys.forest.handler;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestHandlerException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.ForestDataType;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2016-05-04
 */
public class ResultHandler {

    protected boolean isReceivedResponseData(ForestResponse response) {
        if (response == null) return false;
        return response.isReceivedResponseData();
    }


    public Object getResult(ForestRequest request, ForestResponse response, Type resultType, Class resultClass) {
        Object result = response.getResult();
        if (result != null && resultClass.isAssignableFrom(result.getClass())) {
            return result;
        }
        if (isReceivedResponseData(response)) {
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
                        return response.getReceivedDataAsByteArray();
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
                    return response.getReceivedDataAsInputStream();
                }

                ForestDataType dataType = request.getDataType();
                if (dataType.equals(ForestDataType.TEXT)) {
                    return responseText;
                }
                ForestConverter converter = request.getConfiguration().getConverter(dataType);
                return converter.convertToJavaObject(responseText, resultType);

            } catch (Exception e) {
                throw new ForestHandlerException(e, request, response);
            }
        }
        else if (ForestResponse.class.isAssignableFrom(resultClass)) {
            return response;
        }
        return null;
    }

}
