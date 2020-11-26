package com.dtflys.forest.mapping;

/**
 * 字符串模板解析 - 词法标记
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.0.0
 */
public enum  Token {

    /**
     * 布尔值标记
     */
    BOOLEAN(-1, "boolean"),

    /**
     * 整形标记
     */
    INT(0, "int"),

    /**
     * 长整形标记
     */
    LONG(2, "long"),

    /**
     * 单精度浮点数标记
     */
    FLOAT(3, "float"),

    /**
     * 双精度浮点数标记
     */
    DOUBLE(4, "double"),

    /**
     * 字符串标记
     */
    STRING(5, "String"),

    /**
     * 标示符标记
     */
    ID(6, "ID"),

    /**
     * 参数下标标记
     */
    INDEX(7, "INDEX"),

    /**
     * 方法/函数调用标记
     */
    INVOKE(8, "INVOKE"),

    /**
     * 过滤器调用标记
     */
    FILTER_INVOKE(9, "F_INVOKE"),

    /**
     * 点操作标记
     */
    DOT(10, "DOT"),

    /**
     * 变量引用标记
     */
    REF(11, "REF")

    ;

    /**
     * 标记值（魔数）
     */
    private final int value;

    /**
     * 标记名
     */
    private final String name;

    Token(int value, String name) {
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
