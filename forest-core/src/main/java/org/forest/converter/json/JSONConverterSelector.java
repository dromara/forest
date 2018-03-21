package org.forest.converter.json;

import java.io.Serializable;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-05-08 22:21
 */
public class JSONConverterSelector implements Serializable {

    /**
     * check FastJSON
     * @return
     */
    public Class checkFastJSONClass() throws ClassNotFoundException {
        return Class.forName("com.alibaba.fastjson.JSON");
    }

    /**
     * check Jaskon
     * @return
     */
    public Class checkJacsonClass() throws ClassNotFoundException {
        return Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
    }

    /**
     * check Gson
     * @return
     */
    public Class checkGsonClass() throws ClassNotFoundException {
        return Class.forName("com.google.gson.JsonParser");
    }

    public ForestJsonConverter select() {
        try {
            checkFastJSONClass();
            return new ForestFastjsonConverter();
        } catch (Throwable e) {
        }
        try {
            checkJacsonClass();
            return new ForestJacksonConverter();
        } catch (Throwable e1) {
        }
        try {
            checkGsonClass();
            return new ForestGsonConverter();
        } catch (Throwable e) {
        }
        return null;
    }
}
