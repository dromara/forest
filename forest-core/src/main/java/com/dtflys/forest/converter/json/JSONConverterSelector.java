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
     * check FastJSON
     * @return
     */
    public Class checkFastJSONClass() throws Throwable {
        return Class.forName("com.alibaba.fastjson.JSON");
    }

    /**
     * check Jaskon
     * @return
     */
    public Class checkJacsonClass() throws Throwable {
        return Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
    }

    /**
     * check Gson
     * @return
     */
    public Class checkGsonClass() throws Throwable {
        return Class.forName("com.google.gson.JsonParser");
    }

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
