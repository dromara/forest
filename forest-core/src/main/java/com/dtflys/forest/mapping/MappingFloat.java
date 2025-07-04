package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-09 17:36
 */
public class MappingFloat extends MappingExpr {

    private final float number;

    public MappingFloat(MappingTemplate source, float number) {
        super(source, Token.FLOAT, true);
        this.number = number;
    }

    @Override
    public Object render(VariableScope scope, Object[] args) {
        return number;
    }

    @Override
    public boolean isIterateVariable() {
        return false;
    }

    public float getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "[Float: " + number + "]";
    }

}
