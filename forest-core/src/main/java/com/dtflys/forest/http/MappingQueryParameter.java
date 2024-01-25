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

import com.dtflys.forest.mapping.MappingTemplate;

/**
 * Forest请求URL的Query参数项
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-08-11 12:45
 */
public class MappingQueryParameter extends AbstractQueryParameter<MappingQueryParameter> {

    /**
     * 参数名
     */
    private final String name;

    /**
     * 参数值
     */
    private MappingTemplate value;


    public MappingQueryParameter(ForestQueryMap queries, String name, MappingTemplate value) {
        this(queries, name, value, false, null);
    }

    public MappingQueryParameter(ForestQueryMap queries, String name, MappingTemplate value, Boolean urlencoded, String charset) {
        this(queries, name, value, false, urlencoded, charset);
    }

    public MappingQueryParameter(ForestQueryMap queries, String name, MappingTemplate value, boolean fromUrl, Boolean urlencoded, String charset) {
        super(queries, fromUrl);
        this.name = name;
        this.value = value;
        if (urlencoded != null) {
            this.urlencoded = urlencoded;
        } else if (fromUrl) {
            this.urlencoded = false;
        }
        this.charset = charset;
    }


    public MappingQueryParameter(ForestQueryMap queries, String name) {
        this(queries, name, null, false, null);
    }


    @Override
    public String getName() {
        return name;
    }


    public Object getOriginalValue() {
        return value;
    }

    @Override
    public Object getValue() {
        if (value == null) {
            return null;
        }
        return value.render(queries.request);
    }

    @Override
    public MappingQueryParameter setValue(Object value) {
        if (value instanceof MappingTemplate) {
            this.value = (MappingTemplate) value;
        } else if (value != null) {
            this.value = MappingTemplate.text(String.valueOf(value));
        } else {
            this.value = null;
        }
        return this;
    }

}
