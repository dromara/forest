package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableValueContext;

/**
 * Created by Administrator on 2016/5/17.
 */
public class MappingInteger extends MappingExpr {

    private final int number;

    public MappingInteger(int number) {
        super( Token.INT);
        this.number = number;
    }

    @Override
    public Object render(VariableValueContext valueContext) {
        return number;
    }

    @Override
    public boolean isIterateVariable() {
        return false;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "[Int: " + number + "]";
    }
}
