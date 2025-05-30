package com.dtflys.forest.reflection;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;

@FunctionalInterface
public interface ForestVariable {


    Object getValue(ForestRequest req);
    
    
    default  <R> R getValue(ForestRequest req, Class<R> clazz) {
        Object value = getValue(req);
        if (value == null) {
            return null;
        }
        if (clazz != null) {
            return clazz.cast(value);
        }
        return (R) value;
    }

    default  Object getValueFromScope(VariableScope scope) {
        if (scope != null && scope instanceof ForestRequest) {
            return getValue((ForestRequest) scope);
        }
        return null;
    }

    default  <R> R getValueFromScope(VariableScope scope, Class<R> clazz) {
        if (scope != null && scope instanceof ForestRequest) {
            return getValue((ForestRequest) scope, clazz);
        }
        return null;
    }


}
