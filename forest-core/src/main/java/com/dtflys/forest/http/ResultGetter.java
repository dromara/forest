package com.dtflys.forest.http;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.ResultHandler;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.TypeReference;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public abstract class ResultGetter {

    protected final static ResultHandler HANDLER = new ResultHandler();

    protected final ForestRequest request;

    protected ResultGetter(ForestRequest request) {
        this.request = request;
    }

    protected abstract ForestResponse getResponse();


    public <T> T get(Class<T> clazz) {
        final Object result = HANDLER.getResult(request, getResponse(), clazz);
        if (result == null) {
            return null;
        }
        return (T) result;
    }

    public <T> T get(Type type) {
        final Object result = HANDLER.getResult(request, getResponse(), type);
        if (result == null) {
            return null;
        }
        return (T) result;
    }

    public <T> T get(TypeReference<T> typeReference) {
        final Object result = HANDLER.getResult(request, getResponse(), typeReference.getType());
        if (result == null) {
            return null;
        }
        return (T) result;
    }
    
    public <T> T getByPath(String path, Class<T> clazz) {
        return getByPath(path, (Type) clazz);
    }
    
    public <T> T getByPath(String path, TypeReference<T> typeReference) {
        return getByPath(path, typeReference.getType());
    }
    
    public <T> T getByPath(String path, Type type) {
        try {
            final MappingTemplate pathTemplate = request.getMethod().makeTemplate(path);
            final String pathStr = pathTemplate.render(request.getArguments());
            final String resCharset = getResponse().getCharset();
            final String charset = StringUtils.isBlank(resCharset) ? "UTF-8" : resCharset;
            final Object document = Configuration.defaultConfiguration().jsonProvider().parse(getResponse().getInputStream(), charset);
            final ReadContext ctx = JsonPath.parse(document);
            final Object obj = ctx.read(pathStr);
            final String content = JsonPath.parse(obj).jsonString();
            return request.getConfiguration()
                    .getJsonConverter()
                    .convertToJavaObject(new ByteArrayInputStream(content.getBytes(charset)), type);
        } catch (Throwable th) {
            throw new ForestRuntimeException(th);
        }
    }


    /**
     * 安全处理响应体数据流
     *
     * @param consumer 处理响应体数据流的 Lambda
     * @return {@link ForestResponse}实例
     * @since 1.6.0
     */
    public ResultGetter accept(BiConsumer<InputStream, ForestResponse> consumer) {
        final ForestResponse response = getResponse();
        try (final InputStream in = response.getInputStream()) {
            consumer.accept(in, response);
        } catch (Exception e) {
            throw new ForestRuntimeException(e);
        }
        return this;
    }


    /**
     * 安全处理响应体数据流，并返回结果
     *
     * @return Lambda 函数返回的结果
     * @param function 处理响应体数据流的 Lambda 函数
     * @since 1.6.0
     */
    public <R> R accept(BiFunction<InputStream, ForestResponse, R> function) {
        final ForestResponse response = getResponse();
        try (final InputStream in = response.getInputStream()) {
            final Object ret = function.apply(in, response);
            if (ret == null) {
                return null;
            }
            return (R) ret;
        } catch (Exception e) {
            throw new ForestRuntimeException(e);
        }
    }

}
