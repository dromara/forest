package org.forest.converter;

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
    public boolean checkFastJSONClass() throws ClassNotFoundException {
        Class.forName("com.alibaba.fastjson.JSON");
        return true;
    }

    /**
     * check Jaskon
     * @return
     */
    public boolean checkJacsonClass() throws ClassNotFoundException {
        Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
        return true;
    }

    /**
     * check Gson
     * @return
     */
    public boolean checkGsonClass() throws ClassNotFoundException {
        Class.forName("com.google.gson.JsonParser");
        return true;
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
