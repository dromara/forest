package com.dtflys.forest.handler;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestHandlerException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.lifecycles.file.DownloadLifeCycle;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
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
                if (ForestResponse.class.isAssignableFrom(resultClass)
                        || ForestRequest.class.isAssignableFrom(resultClass)) {
                    if (resultType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) resultType;
                        Class rowClass = (Class) parameterizedType.getRawType();
                        if (ForestResponse.class.isAssignableFrom(rowClass)
                                || ForestRequest.class.isAssignableFrom(resultClass)) {
                            Type realType = parameterizedType.getActualTypeArguments()[0];
                            Class realClass = ReflectUtils.toClass(parameterizedType.getActualTypeArguments()[0]);
                            if (realClass == null) {
                                realClass = String.class;
                            }
                            Object realResult = getResult(request, response, realType, realClass);
                            response.setResult(realResult);
                        }
                    } else {
                        Object realResult = getResult(request, response, Object.class, Object.class);
                        response.setResult(realResult);
                    }
                    return response;
                }
                if (Future.class.isAssignableFrom(resultClass)) {
                    if (resultType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) resultType;
                        Class rowClass = (Class) parameterizedType.getRawType();
                        if (Future.class.isAssignableFrom(rowClass)) {
                            Type realType = parameterizedType.getActualTypeArguments()[0];
                            Class realClass = ReflectUtils.toClass(parameterizedType.getActualTypeArguments()[0]);
                            return getResult(request, response, realType, realClass);
                        }
                    }
                }
                if (resultClass.isArray()) {
                    if (byte[].class.isAssignableFrom(resultClass)) {
                        return response.getByteArray();
                    }
                }
                Object attFile = request.getAttachment(DownloadLifeCycle.ATTACHMENT_NAME_FILE);
                if (attFile != null && attFile instanceof File) {
                    ForestConverter converter = request.getConfiguration().getConverter(ForestDataType.JSON);
                    return converter.convertToJavaObject(attFile, resultClass);
                }
                String responseText = null;
                if (result != null && CharSequence.class.isAssignableFrom(result.getClass())) {
                    responseText = result.toString();
                }
                else if (CharSequence.class.isAssignableFrom(resultClass)) {
                    try {
                        responseText = response.readAsString();
                    } catch (Throwable th) {
                        request.getLifeCycleHandler().handleError(request, response, th);
                    }
                }
                else {
                    try {
                        responseText = response.getContent();
                    } catch (Throwable th) {
                        request.getLifeCycleHandler().handleError(request, response, th);
                    }
                }
                response.setContent(responseText);
                if (CharSequence.class.isAssignableFrom(resultClass)) {
                    return responseText;
                }
                if (InputStream.class.isAssignableFrom(resultClass)) {
                    return response.getInputStream();
                }
                ContentType contentType = response.getContentType();
                if (request.getDecoder() != null) {
                    if (contentType != null && contentType.canReadAsString()) {
                        return request.getDecoder().convertToJavaObject(responseText, resultType);
                    } else {
                        return request.getDecoder().convertToJavaObject(response.getByteArray(), resultType);
                    }
                }

                ForestDataType dataType = request.getDataType();
                ForestConverter converter = request.getConfiguration().getConverter(dataType);
                if (contentType != null && contentType.canReadAsString()) {
                    return converter.convertToJavaObject(responseText, resultType);
                }
                Charset charset = null;
                String contentEncoding = response.getContentEncoding();
                if (contentEncoding != null) {
                    charset = Charset.forName(contentEncoding);
                }
                return converter.convertToJavaObject(response.getByteArray(), resultType, charset);
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
