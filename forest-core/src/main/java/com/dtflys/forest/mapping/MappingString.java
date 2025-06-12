package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MappingString extends MappingExpr {

    private final String text;

    public String getText() {
        return text;
    }

    public MappingString(String text, int startIndex, int endIndex) {
        super(null, Token.STRING);
        this.text = text;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public Object render(VariableScope scope, Object[] args) {
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
