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
public class JSONConvertSelector implements Serializable {

    /**
     * check FastJSON
     * @return
     */
    public boolean checkFastJSONClass() {
        Class toLoadClass = null;
        try {
            toLoadClass = Class.forName("com.alibaba.fastjson.JSON");
        } catch (ClassNotFoundException e) {
        }
        return toLoadClass != null;
    }

    /**
     * check Jaskon
     * @return
     */
    public boolean checkJacsonClass() {
        Class toLoadClass = null;
        try {
            toLoadClass = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
        } catch (ClassNotFoundException e) {
        }
        return toLoadClass != null;
    }

    /**
     * check Gson
     * @return
     */
    public boolean checkGsonClass() {
        Class toLoadClass = null;
        try {
            toLoadClass = Class.forName("com.google.gson.JsonParser");
        } catch (ClassNotFoundException e) {
        }
        return toLoadClass != null;
    }

    public ForestJsonConverter select() {
        if (checkFastJSONClass()) {
            return new ForestFastjsonConverter();
        }
        if (checkJacsonClass()) {
            return new ForestJacksonConverter();
        }
        if (checkGsonClass()) {
            return new ForestGsonConverter();
        }
        return null;
    }
}
