package com.dtflys.forest.result;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.ForestMethod;

import java.lang.reflect.Type;

/**
 * 返回类型处理器: 调用接口方法时返回结果
 *
 * @param <T> 返回结果的类型泛型
 * @since 1.6.0
 */
public abstract class ReturnOnInvokeMethodTypeHandler<T> extends ResultTypeHandler<T> {

    public ReturnOnInvokeMethodTypeHandler(Class<T> resultClass) {
        super(ReturnFlag.RETURN_ON_INVOKE_METHOD, resultClass);
    }


    public abstract T getReturnValue(Type returnType, Type onSuccessClassGenericType, ForestMethod forestMethod, Object[] args, ForestRequest request);

}
