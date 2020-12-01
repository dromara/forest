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

package com.dtflys.forest.converter.json;

import java.io.Serializable;

/**
 * JSON转换器选择策略
 * 此类负责选择对应的可用JSON转转器供Forest使用
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-05-08 22:21
 */
public class JSONConverterSelector implements Serializable {

    private static JSONConverterSelector instance;

    private ForestJsonConverter cachedJsonConverter;

    public static JSONConverterSelector getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new JSONConverterSelector();
        return instance;
    }


    /**
     * 检测FastJSON相关类型
     * @return FastJSON相关类型
     */
    public Class checkFastJSONClass() throws Throwable {
        return Class.forName("com.alibaba.fastjson.JSON");
    }

    /**
     * 检测Jaskon相关类型
     * @return Jaskon相关类型
     */
    public Class checkJacsonClass() throws Throwable {
        return Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
    }

    /**
     * 检测Gson相关类型
     * @return Gson相关类型
     */
    public Class checkGsonClass() throws Throwable {
        return Class.forName("com.google.gson.JsonParser");
    }

    /**
     * 选择Forest的JSON转换器
     * <p>从FastJson、Jackson以及Gson中动态选择一个可用的JSON转换器</p>
     *
     * @return Forest的JSON转换器，{@link ForestJsonConverter}接口实例
     */
    public ForestJsonConverter select() {
        if (cachedJsonConverter != null) {
            return cachedJsonConverter;
        }
        try {
            checkFastJSONClass();
            cachedJsonConverter = new ForestFastjsonConverter();
            return cachedJsonConverter;
        } catch (Throwable e) {
        }
        try {
            checkJacsonClass();
            cachedJsonConverter = new ForestJacksonConverter();
            return cachedJsonConverter;
        } catch (Throwable e1) {
        }
        try {
            checkGsonClass();
            cachedJsonConverter = new ForestGsonConverter();
        } catch (Throwable e) {
        }
        return cachedJsonConverter;
    }
}
