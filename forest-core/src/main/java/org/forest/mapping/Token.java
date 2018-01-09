package org.forest.mapping;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-09 18:38
 */
public class Token {
    public final static Token BOOLEAN = new Token(-1, "boolean");
    public final static Token INT = new Token(0, "int");
    public final static Token LONG = new Token(2, "long");
    public final static Token FLOAT = new Token(3, "float");
    public final static Token DOUBLE = new Token(4, "double");
    public final static Token STRING = new Token(5, "String");
    public final static Token ID = new Token(6, "ID");
    public final static Token INDEX = new Token(7, "INDEX");
    public final static Token INVOKE = new Token(8, "INVOKE");
    public final static Token FINVOKE = new Token(9, "INVOKE");
    public final static Token DOT = new Token(10, "DOT");
    public final static Token REF = new Token(11, "REF");

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
