package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

import java.io.InputStream;

/**
 * 回调函数: 处理数据流时调用
 * 
 * @since 1.6.0
 */
@FunctionalInterface
public interface OnStream<R> {

    /**
     * 回调函数: 处理数据流时调用
     * <p>在该函数被回调时，HTTP 响应种的数据流已被自动打开，调用结束后也会自动关闭流</p>
     * 
     * @param in 数据流: {@link InputStream}对象
     * @param req Forest 请求对象
     * @param res Forest 响应对象
     * @since 1.6.0
     */
    R onStream(InputStream in, ForestRequest req, ForestResponse res);
}
