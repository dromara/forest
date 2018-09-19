package com.dtflys.forest.mapping;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-09 17:49
 */
public class MappingBoolean extends MappingExpr {

    private boolean value;

    protected MappingBoolean(boolean value) {
        super(Token.BOOLEAN);
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public Object render(Object[] args) {
        return value;
    }

    @Override
    public String toString() {
        return "[BOOL: " + value + "]";
    }
}
