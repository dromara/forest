package com.dtflys.forest.converter;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.ForestDataType;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Forest的数据转换器
 * 转换器包含序列化以及反序列化过程
 *
 * Created by Gongjun on 2016/5/26.
 */
public interface ForestConverter<S> {

    /**
     * 将源数据转换为目标类型（Class）的java对象
     * @param source       源数据
     * @param targetType   目标类型 (Class对象)
     * @param <T>          目标类型泛型
     * @return 转换后的目标类型对象
     */
    default  <T> T convertToJavaObject(S source, Class<T> targetType) {
        return convertToJavaObject(source, (Type) targetType);
    }

    /**
     * 将源数据转换为目标类型（Type）的java对象
     * @param source       源数据
     * @param targetType   目标类型 (Type对象)
     * @param <T>          目标类型泛型
     * @return 转换后的目标类型对象
     */
    <T> T convertToJavaObject(S source, Type targetType);

    /**
     * 将源数据转换为目标类型（Class）的java对象
     *
     * @param source 源数据
     * @param targetType 目标类型 (Class对象)
     * @param charset 字符集
     * @return 转换后的目标类型对象
     * @param <T> 目标类型泛型
     */
    <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset);

    /**
     * 将源数据转换为目标类型（Type）的java对象
     *
     * @param source 源数据
     * @param targetType 目标类型 (Type对象)
     * @param charset 字符集
     * @return 转换后的目标类型对象
     * @param <T> 目标类型泛型
     */
    <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset);


    default  <T> T convertToJavaObject(InputStream source, Class<T> targetType) {
        return convertToJavaObject(source, targetType, StandardCharsets.UTF_8);
    }

    default  <T> T convertToJavaObject(InputStream source, Type targetType) {
        return convertToJavaObject(source, targetType, StandardCharsets.UTF_8);
    }

    default  <T> T convertToJavaObject(InputStream source, Class<T> targetType, Charset charset) {
        return convertToJavaObject(source, targetType, charset);
    }

    default  <T> T convertToJavaObject(InputStream source, Type targetType, Charset charset) {
        try {
            return convertToJavaObject(IOUtils.toByteArray(source), targetType, charset);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }



    /**
     * 获取当前数据转换器转换类型
     *
     * @return {@link ForestDataType} 对象实例
     */
    ForestDataType getDataType();
}
