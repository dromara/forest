package com.dtflys.forest.mapping;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MappingString extends MappingExpr {

    private final String text;

    public String getText() {
        return text;
    }

    public MappingString(String text) {
        super(Token.STRING);
        this.text = text;
    }

    @Override
    public Object render(Object[] args) {
        return getText();
    }

    @Override
    public String toString() {
        return text.toString();
    }
}
