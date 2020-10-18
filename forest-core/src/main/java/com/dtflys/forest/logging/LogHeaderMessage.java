package com.dtflys.forest.logging;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-14 17:31
 */
public class LogHeaderMessage {

    private final String name;

    private final String value;

    public LogHeaderMessage(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
