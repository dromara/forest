package com.dtflys.forest.reflection;

import com.dtflys.forest.http.ForestRequest;

public class ConstantVariable extends BasicVariable {

    public ConstantVariable(Object value) {
        super(value);
    }

    @Override
    public Object getValue(ForestRequest request) {
        return value;
    }
}
