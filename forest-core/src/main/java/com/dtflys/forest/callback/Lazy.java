package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestRequest;

/**
 * Forest 延迟求值 Lambda 接口
 *
 * @param <T> Lambda 返回值类型
 * @author gongjun
 * @since 1.5.29
 */
@FunctionalInterface
public interface Lazy<T> {

    /**
     * 调用 Lambda 进行求值
     *
     * @param req Forest 请求对象
     * @return 延迟求值的结果
     */
    T getValue(ForestRequest req);
}
