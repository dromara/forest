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
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public abstract class ResultGetter implements ForestResultGetter {

    protected final static ResultHandler HANDLER = new ResultHandler();

    protected final ForestRequest request;

    protected ResultGetter(ForestRequest request) {
        this.request = request;
    }

    protected abstract ForestResponse getResponse();


    @Override
    public <T> T get(Class<T> clazz) {
        final Optional retOpt = getResponse().result;
        final Object result = HANDLER.getResult(retOpt, request, getResponse(), clazz);
        if (result == null) {
            return null;
        }
        return (T) result;
    }

    @Override
    public <T> T get(Type type) {
        final Optional retOpt = getResponse().result;
        final Object result = HANDLER.getResult(retOpt, request, getResponse(), type);
        if (result == null) {
            return null;
        }
        return (T) result;
    }

    @Override
    public <T> T get(TypeReference<T> typeReference) {
        final Optional retOpt = getResponse().result;
        final Object result = HANDLER.getResult(retOpt, request, getResponse(), typeReference.getType());
        if (result == null) {
            return null;
        }
        return (T) result;
    }

    @Override
    public <T> Optional<T> opt(Class<T> clazz) {
        return Optional.ofNullable(get(clazz));
    }

    @Override
    public <T> Optional<T> opt(Type type) {
        return Optional.ofNullable(get(type));
    }

    @Override
    public <T> Optional<T> opt(TypeReference<T> typeReference) {
        return Optional.ofNullable(get(typeReference));
    }

    @Override
    public <T> T getByPath(String path, Class<T> clazz) {
        return getByPath(path, (Type) clazz);
    }
    
    @Override
    public <T> T getByPath(String path, TypeReference<T> typeReference) {
        return getByPath(path, typeReference.getType());
    }
    
    
    @Override
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
    @Override
    public ResultGetter openStream(BiConsumer<InputStream, ForestResponse> consumer) {
        final ForestResponse response = getResponse();
        try (final InputStream in = response.getInputStream()) {
            consumer.accept(in, response);
        } catch (Exception e) {
            if (e instanceof ForestRuntimeException) {
                throw (ForestRuntimeException) e;
            }
            throw new ForestRuntimeException(e);
        } finally {
            response.close();
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
    @Override
    public <R> R openStream(BiFunction<InputStream, ForestResponse, R> function) {
        final ForestResponse response = getResponse();
        try (final InputStream in = response.getInputStream()) {
            final Object ret = function.apply(in, response);
            if (ret == null) {
                return null;
            }
            return (R) ret;
        } catch (Exception e) {
            throw new ForestRuntimeException(e);
        } finally {
            response.close();
        }
    }

}
