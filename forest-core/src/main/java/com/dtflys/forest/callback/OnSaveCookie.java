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

package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;

/**
 * 回调函数: 需要保存Cookie时调用
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-RC1
 */
public interface OnSaveCookie {

    /**
     * 在请求响应成功后，需要保存Cookie时调用该方法
     *
     * @param req Forest请求对象
     * @param cookies Cookie集合，通过响应返回的Cookie都从该集合获取
     */
    void onSaveCookie(ForestRequest req, ForestCookies cookies);
}
