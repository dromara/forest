package com.dtflys.forest.mapping;


import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;

/**
 * Created by Administrator on 2016/5/17.
 */
public class MappingInteger extends MappingExpr {

    private final int number;

    public MappingInteger(MappingTemplate source, int number) {
        super(source, Token.INT, true);
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

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "[Int: " + number + "]";
    }
}
