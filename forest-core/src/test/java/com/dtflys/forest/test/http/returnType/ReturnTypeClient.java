package com.dtflys.forest.test.http.returnType;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Return;
import com.dtflys.forest.utils.TypeReference;

import java.lang.reflect.Type;

@Address(host = "localhost", port = "{port}")
public interface ReturnTypeClient {

    /**
     * 通过 Class 类型参数来标识返回类型
     *
     * @param clazz 返回类型
     * @return clazz 参数所标识的返回类型的实例
     * @param <T> 未知泛型参数，通过传入 clazz 参数来明确泛型的实际类型
     */
    @Get("/")
    <T> T getGenericClass(@Return Class<T> clazz);

    /**
     * 通过 Type 类型参数来标识返回类型
     *
     * @param type 返回类型
     * @return type 参数所标识的返回类型的实例
     * @param <T> 未知泛型参数，通过传入 type 参数来明确泛型的实际类型
     */
    @Get("/")
    <T> T getGenericType(@Return Type type);

    /**
     * 通过 TypeReference 类型参数来标识返回类型
     *
     * @param typeReference 返回类型
     * @return typeReference 参数所标识的返回类型的实例
     * @param <T> 未知泛型参数，通过传入 typeReference 参数来明确泛型的实际类型
     */
    @Get("/")
    <T> T getGenericTypeReference(@Return TypeReference<T> typeReference);

}
