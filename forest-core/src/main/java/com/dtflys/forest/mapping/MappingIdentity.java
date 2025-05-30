package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-09 16:37
 */
public class MappingIdentity extends MappingExpr {

    private String name;

    public MappingIdentity(String name, int startIndex, int endIndex) {
        super(null, Token.ID);
        this.name = name;
        setIndexRange(startIndex, endIndex);
    }

    public String getName() {
        return name;
    }

    @Override
    public Object render(RequestVariableScope scope, Object[] args) {
        return name;
    }

    @Override
    public boolean isIterateVariable() {
        return false;
    }

    @Override
    public String toString() {
        return "[ID: " + name + "]";
    }
}
