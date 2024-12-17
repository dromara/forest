package com.dtflys.forest.http;

import com.dtflys.forest.utils.TypeReference;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface ForestResultGetter {
    
    <T> T get(Class<T> clazz);

    <T> T get(Type type);

    <T> T get(TypeReference<T> typeReference);

    <T> Optional<T> opt(Class<T> clazz);

    <T> Optional<T> opt(Type type);

    <T> Optional<T> opt(TypeReference<T> typeReference);

    <T> T getByPath(String path, Class<T> clazz);

    <T> T getByPath(String path, TypeReference<T> typeReference);

    <T> T getByPath(String path, Type type);

    ResultGetter openStream(BiConsumer<InputStream, ForestResponse> consumer);

    <R> R openStream(BiFunction<InputStream, ForestResponse, R> function);
}
