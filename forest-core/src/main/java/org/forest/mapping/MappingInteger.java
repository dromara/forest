package org.forest.mapping;

/**
 * Created by Administrator on 2016/5/17.
 */
public class MappingInteger extends MappingExpr {

    private final Integer number;

    public MappingInteger(Integer number) {
        this.number = number;
    }

    @Override
    public Object render(Object[] args) {
        return args[number];
    }

    public Integer getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "[Int: " + number + "]";
    }
}
