package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableValueContext;

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
    public Object render(VariableValueContext valueContext) {
        return getText();
    }

    @Override
    public boolean isIterateVariable() {
        return false;
    }

    @Override
    public String toString() {
        return text.toString();
    }
}
