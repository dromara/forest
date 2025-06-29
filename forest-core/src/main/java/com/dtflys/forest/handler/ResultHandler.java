package com.dtflys.forest.handler;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestHandlerException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.http.Res;
import com.dtflys.forest.http.UnclosedResponse;
import com.dtflys.forest.lifecycles.file.DownloadLifeCycle;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;
import com.dtflys.forest.sse.ForestSSEListener;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2016-05-04
 */
public class ResultHandler {

    protected boolean isReceivedResponseData(Res response) {
        if (response == null) {
            return false;
        }
        return response.isReceivedResponseData();
    }

    /**
     * 进行转换并获取结果
     *
     * @param request Forest 请求对象
     * @param response Forest 响应对象
     * @param resultType {@link Type} 实例
     * @return 转换后的对象
     * @since 1.5.27
     */
    public Object getResult(ForestRequest request, Res response, Type resultType) {
        final Class<?> clazz = ReflectUtils.toClass(resultType);
        return getResult(null, request, response, resultType, clazz);
    }

    public Object getResult(Optional<?> resultOpt, ForestRequest request, Res response, Type resultType) {
        final Class<?> clazz = ReflectUtils.toClass(resultType);
        return getResult(resultOpt, request, response, resultType, clazz);
    }


    /**
     * 进行转换并获取结果
     *
     * @param request Forest 请求对象
     * @param response Forest 响应对象
     * @param resultClass {@link Class} 实例
     * @return 转换后的对象
     * @since 1.5.27
     */
    public Object getResult(ForestRequest request, Res response, Class resultClass) {
        final Type type = ReflectUtils.toType(resultClass);
        return getResult(null, request, response, type, resultClass);
    }

    public Object getResult(Optional<?> resultOpt, ForestRequest request, Res response, Class resultClass) {
        final Type type = ReflectUtils.toType(resultClass);
        return getResult(resultOpt, request, response, type, resultClass);
    }



    public Object getResult(Optional<?> resultOpt, ForestRequest request, Res response, Type resultType, Class resultClass) {
        String optStringValue = null;
        if (resultOpt != null) {
            if (Optional.class.isAssignableFrom(resultClass)) {
                return resultOpt;
            }
            final Object optValue = resultOpt.orElse(null);
            if (optValue == null) {
                return null;
            }
            final Class<?> optValueClass = optValue.getClass();
            if (ReflectUtils.isAssignableFrom(resultClass, optValueClass)) {
                return optValue;
            }
            if (Res.class.isAssignableFrom(resultClass)) {
                final ParameterizedType parameterizedType = ReflectUtils.toParameterizedType(resultType);
                if (parameterizedType == null) {
                    response.setResult(optValue);
                    return response;
                }
                final Type[] argTypes = parameterizedType.getActualTypeArguments();
                if (argTypes.length == 0) {
                    response.setResult(optValue);
                    return response;
                }
                final Type argType = argTypes[0];
                final Class argClass = ReflectUtils.toClass(argType);
                if (argClass.isAssignableFrom(optValueClass)) {
                    response.setResult(optValue);
                    return response;
                }
            }
            if (ReflectUtils.isPrimaryType(optValueClass)) {
                optStringValue = String.valueOf(optValue);
            }
        }
        if (request.isDownloadFile()) {
            return null;
        }
        if (isReceivedResponseData(response)) {
            try {
                if (void.class.isAssignableFrom(resultClass) || Void.class.isAssignableFrom(resultClass)) {
                    return null;
                }
                // 处理特殊泛型类型 （Res, ForestRequest, Optional）
                if (Res.class.isAssignableFrom(resultClass)
                        || ForestRequest.class.isAssignableFrom(resultClass)
                        || Optional.class.isAssignableFrom(resultClass)) {
                    if (resultType instanceof ParameterizedType) {
                        final ParameterizedType parameterizedType = (ParameterizedType) resultType;
                        final Class<?> rowClass = (Class<?>) parameterizedType.getRawType();
                        if (Res.class.isAssignableFrom(rowClass)
                                || ForestRequest.class.isAssignableFrom(resultClass)
                                || Optional.class.isAssignableFrom(rowClass)) {
                            
                            final Type realType = parameterizedType.getActualTypeArguments()[0];
                            Class<?> realClass = ReflectUtils.toClass(parameterizedType.getActualTypeArguments()[0]);
                            if (realClass == null) {
                                realClass = String.class;
                            }
                            if (!(UnclosedResponse.class.isAssignableFrom(rowClass))) {
                                final Object realResult = getResult(resultOpt, request, response, realType, realClass);
                                response.setResult(realResult);
                            }
                            
                        }
                    }
                    return response;
                }
                if (Future.class.isAssignableFrom(resultClass)) {
                    if (resultType instanceof ParameterizedType) {
                        final ParameterizedType parameterizedType = (ParameterizedType) resultType;
                        final Class<?> rowClass = (Class<?>) parameterizedType.getRawType();
                        if (Future.class.isAssignableFrom(rowClass)) {
                            final Type realType = parameterizedType.getActualTypeArguments()[0];
                            final Class<?> realClass = ReflectUtils.toClass(parameterizedType.getActualTypeArguments()[0]);
                            if (realClass == null) {
                                return ((MethodLifeCycleHandler<?>) request.getLifeCycleHandler()).getResultData();
                            }
                            return getResult(resultOpt, request, response, realType, realClass);
                        }
                    }
                }
                if (ForestSSEListener.class.isAssignableFrom(resultClass)) {
                    if (ForestSSE.class.equals(resultClass)) {
                        return request.sse();
                    }
                    return request.sse(resultClass);
                }
                if (resultClass.isArray()) {
                    if (byte[].class.isAssignableFrom(resultClass)) {
                        return response.getByteArray();
                    }
                }
                final Object attFile = request.getAttachment(DownloadLifeCycle.ATTACHMENT_NAME_FILE);
                if (attFile != null && attFile instanceof File) {
                    final ForestConverter converter = request.getConfiguration().getConverter(ForestDataType.JSON);
                    return converter.convertToJavaObject(attFile, resultClass);
                }
                String responseText = null;
                if (CharSequence.class.isAssignableFrom(resultClass)) {
                    try {
                        responseText = optStringValue != null ? optStringValue : response.readAsString();
                    } catch (Throwable th) {
                        request.getLifeCycleHandler().handleError(request, (ForestResponse) response, th);
                    }
                }
                else {
                    try {
                        responseText = optStringValue != null ? optStringValue : response.getContent();
                    } catch (Throwable th) {
                        request.getLifeCycleHandler().handleError(request, (ForestResponse) response, th);
                    }
                }
                response.setContent(responseText);
                if (InputStream.class.isAssignableFrom(resultClass)) {
                    return response.getInputStream();
                }
                final ContentType contentType = response.getContentType();
                final ForestConverter decoder = request.getDecoder();
                if (decoder != null) {
                    if (contentType != null && contentType.canReadAsString()) {
                        return decoder.convertToJavaObject(responseText, resultType);
                    } else {
                        final String charset = response.getCharset();
                        return decoder.convertToJavaObject(
                                response.getByteArray(), resultType, Charset.forName(Optional.ofNullable(charset).orElse("UTF-8")));
                    }
                } else if (CharSequence.class.isAssignableFrom(resultClass)) {
                    return responseText;
                }

                final ForestDataType dataType = request.getDataType();
                final ForestConverter converter = decoder != null ? decoder : request.getConfiguration().getConverter(dataType);
                if (contentType != null && contentType.canReadAsString()) {
                    return converter.convertToJavaObject(responseText, resultType);
                }
                Charset charset = null;
                String resCharset  = response.getCharset();
                if (resCharset != null) {
                    charset = Charset.forName(resCharset);
                }
                if (optStringValue != null) {
                    return converter.convertToJavaObject(optStringValue.getBytes(StandardCharsets.UTF_8), resultType, charset);
                }
                return converter.convertToJavaObject(response.getInputStream(), resultType, charset);
            } catch (Exception e) {
                throw new ForestHandlerException(e, request, (ForestResponse) response);
            }
        }
        else if (Res.class.isAssignableFrom(resultClass)) {
            return response;
        }
        return null;
    }

}
