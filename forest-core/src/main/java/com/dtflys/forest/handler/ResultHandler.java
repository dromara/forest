package com.dtflys.forest.handler;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestHandlerException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Future;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2016-05-04
 */
public class ResultHandler {

    protected boolean isReceivedResponseData(ForestResponse response) {
        if (response == null) {
            return false;
        }
        return response.isReceivedResponseData();
    }


    public Object getResult(ForestRequest request, ForestResponse response, Type resultType, Class resultClass) {
        if (request.isDownloadFile()) {
            return null;
        }
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
                    if (resultType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) resultType;
                        Class rowClass = (Class) parameterizedType.getRawType();
                        if (ForestResponse.class.isAssignableFrom(rowClass)) {
                            Type realType = parameterizedType.getActualTypeArguments()[0];
                            Class realClass = ReflectUtils.getClassByType(parameterizedType.getActualTypeArguments()[0]);
                            Object realResult = getResult(request, response, realType, realClass);
                            response.setResult(realResult);
                        }
                    }
                    return response;
                }
                if (Future.class.isAssignableFrom(resultClass)) {
                    if (resultType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) resultType;
                        Class rowClass = (Class) parameterizedType.getRawType();
                        if (Future.class.isAssignableFrom(rowClass)) {
                            Type realType = parameterizedType.getActualTypeArguments()[0];
                            Class realClass = ReflectUtils.getClassByType(parameterizedType.getActualTypeArguments()[0]);
                            return getResult(request, response, realType, realClass);
                        }
                    }
                }
                if (boolean.class.isAssignableFrom(resultClass) || Boolean.class.isAssignableFrom(resultClass)) {
                    return response.isSuccess();
                }
                if (resultClass.isArray()) {
                    if (byte[].class.isAssignableFrom(resultClass)) {
                        return response.getByteArray();
                    }
                }
                Object attFile = request.getAttachment("file");
                if (attFile != null && attFile instanceof File) {
                    ForestConverter converter = request.getConfiguration().getConverter(ForestDataType.JSON);
                    return converter.convertToJavaObject(attFile, resultClass);
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
                    return response.getInputStream();
                }

                if (request.getDecoder() != null) {
                    return request.getDecoder().convertToJavaObject(responseText, resultType);
                }

                ForestDataType dataType = request.getDataType();
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
