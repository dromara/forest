package org.dromara.forest.http;

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
    T getValue(ForestRequest<?> req);

    /**
     * 当前延迟参数是否正在求值
     *
     * @param req 请求对象
     * @return 是否正在求值
     */
    default boolean isEvaluating(ForestRequest<?> req) {
        return req.evaluatingLazyValueStack.stream().anyMatch(val -> val == this);
    }


    static boolean isEvaluatingLazyValue(Object value, ForestRequest<?> request) {
        return value instanceof Lazy && ((Lazy<?>) value).isEvaluating(request);
    }

    /**
     * 进行求值
     *
     * @param req 请求对象
     * @return 求值结果
     */
    default T eval(ForestRequest<?> req) {
        req.evaluatingLazyValueStack.push(this);
        try {
            return this.getValue(req);
        } finally {
            req.evaluatingLazyValueStack.pop();
        }
    }
}
