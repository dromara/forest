/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.dromara.forest.callback;

import org.dromara.forest.http.ForestAddress;
import org.dromara.forest.http.ForestRequest;

/**
 * 主机地址信息动态来源接口
 * <P>
 * 本质是一个回调函数: 在创建请求 URL 地址时被调用
 * <p>用于动态构建请求 URL 的主机地址部分
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
@FunctionalInterface
public interface AddressSource {

    /**
     * 获取地址信息
     * <p>本质是调用回调函数: 在创建请求 URL 地址时被调用
     * <p>用于动态构建请求 URL 的主机地址部分
     *
     * @param req Forest请求对象
     * @return 注解地址信息, {@link ForestAddress}对象实例
     */
    ForestAddress getAddress(ForestRequest req);

}
