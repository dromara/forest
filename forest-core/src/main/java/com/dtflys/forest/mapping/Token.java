package com.dtflys.forest.mapping;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-09 18:38
 */
public enum  Token {
    BOOLEAN(-1, "boolean"),
    INT(0, "int"),
    LONG(2, "long"),
    FLOAT(3, "float"),
    DOUBLE(4, "double"),
    STRING(5, "String"),
    ID(6, "ID"),
    INDEX(7, "INDEX"),
    INVOKE(8, "INVOKE"),
    FINVOKE(9, "INVOKE"),
    DOT(10, "DOT"),
    REF(11, "REF")

    ;


    private final int value;
    private final String name;

    private Token(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
