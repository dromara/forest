package com.dtflys.forest.reflection;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;

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
        if (scope instanceof RequestVariableScope) {
            return getValue(((RequestVariableScope) scope).asRequest());
        }
        return getValue(null);
    }

    default  <R> R getValueFromScope(VariableScope scope, Class<R> clazz) {
        if (scope instanceof RequestVariableScope) {
            return getValue(((RequestVariableScope) scope).asRequest(), clazz);
        }
        return getValue(null, clazz);
    }


    default void addSubscriber(VariableSubscriber subscriber) {
        subscriber.setAlwaysChanged(true);
    }
}
