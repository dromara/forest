package com.dtflys.forest.http;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.ResultHandler;
import com.dtflys.forest.utils.TypeReference;

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
