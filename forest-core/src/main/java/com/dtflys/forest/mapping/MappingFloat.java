package com.dtflys.forest.mapping;

import com.dtflys.forest.reflection.ForestMethod;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-09 17:36
 */
public class MappingFloat extends MappingExpr {

    private final float number;

    public MappingFloat(float number) {
        super(null, Token.FLOAT);
        this.number = number;
    }

    @Override
    public Object render(Object[] args) {
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
