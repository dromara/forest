package com.dtflys.forest.http;

/**
 * HTTP协议枚举
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
public enum ForestProtocol {

    HTTP_1_0("http 1.0"),

    HTTP_1_1("http 1.1"),

    HTTP_2("http 2")
    ;

    private final String name;

    ForestProtocol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
