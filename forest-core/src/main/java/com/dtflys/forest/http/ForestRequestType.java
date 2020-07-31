package com.dtflys.forest.http;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;

public enum ForestRequestType {
    GET("GET", true),
    POST("POST", false),
    PUT("PUT", false),
    PATCH("PATCH", false),
    HEAD("HEAD", true),
    OPTIONS("OPTIONS", true),
    DELETE("DELETE", true),
    TRACE("TRACE", true),
    ;

    private final String name;
    private final boolean defaultParamInQuery;

    ForestRequestType(String name, boolean defaultParamInQuery) {
        this.name = name;
        this.defaultParamInQuery = defaultParamInQuery;
    }

    public String getName() {
        return name;
    }

    public boolean isDefaultParamInQuery() {
        return defaultParamInQuery;
    }

    public boolean match(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        return this.name.equals(name.toUpperCase());
    }

    public static ForestRequestType findType(String name) {
        for (ForestRequestType type : ForestRequestType.values()) {
            if (type.match(name)) {
                return type;
            }
        }
        throw new ForestRuntimeException("Http request type \"" + name + "\" is not be supported.");
    }

}
