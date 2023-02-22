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

package com.dtflys.forest.http;

import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.http.body.ObjectRequestBody;
import com.dtflys.forest.http.body.StringRequestBody;
import com.dtflys.forest.utils.ForestDataType;

/**
 * Forest请求体
 * <p>该类为Forest请求中所有类型请求体项的父类</p>
 * <p>该类为抽象类，主要有以下3个子类: </p>
 * <p>    {@link StringRequestBody}    字符串请求体</p>
 * <p>    {@link ObjectRequestBody}    对象请求体</p>
 * <p>    {@link NameValueRequestBody} 键值对请求体</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-22 17:30
 */
public abstract class ForestRequestBody {

    /**
     * 所对应的请求体对象
     */
    protected ForestBody body;

    /**
     * 默认值
     */
    private String defaultValue;

    public ForestBody getBody() {
        return body;
    }

    void setBody(ForestBody body) {
        this.body = body;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public ForestRequestBody setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public abstract byte[] getByteArray();

    public abstract ForestDataType getDefaultBodyType();
}
