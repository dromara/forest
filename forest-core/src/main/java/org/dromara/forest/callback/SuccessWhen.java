package org.dromara.forest.callback;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;

/**
 * 回调函数: 请求是否成功
 * <p>该回调函数用于判断请求是否成功
 * <p>如果成功, 执行 onSuccess
 * <p>如果失败, 执行 onError
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
@FunctionalInterface
public interface SuccessWhen {

    /**
     * 回调函数: 请求是否成功
     * <p>该回调函数用于判断请求是否成功
     * <p>如果成功, 执行 onSuccess
     * <p>如果失败, 执行 onError
     *
     * @param req Forest请求对象
     * @param res Forest响应对象
     * @return {@code true}: 请求成功, {@code false}: 请求失败
     */
    boolean successWhen(ForestRequest req, ForestResponse res);

}
