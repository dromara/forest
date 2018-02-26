package org.forest.converter.json;

import org.forest.converter.json.ForestFastjsonConverter;
import org.forest.converter.json.ForestGsonConverter;
import org.forest.converter.json.ForestJacksonConverter;
import org.forest.converter.json.ForestJsonConverter;

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
        } catch (ClassNotFoundException e) {
        }
        try {
            checkJacsonClass();
            return new ForestJacksonConverter();
        } catch (ClassNotFoundException e1) {
        }
        try {
            checkGsonClass();
            return new ForestGsonConverter();
        } catch (ClassNotFoundException e) {
        }
        return null;
    }
}
