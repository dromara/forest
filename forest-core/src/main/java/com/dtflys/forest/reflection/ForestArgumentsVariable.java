package com.dtflys.forest.reflection;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;

@FunctionalInterface
public interface ForestArgumentsVariable extends ForestVariable {
    
    default Object getValue(ForestRequest req) {
        return getValue(req, req != null ? req.arguments() : new Object[0]);
    }
    
    Object getValue(ForestRequest req, Object[] args);

    default Object getValueFromScope(VariableScope scope, Object[] args) {
        if (scope instanceof RequestVariableScope) {
            return getValue(((RequestVariableScope) scope).asRequest(), args);
        }
        return getValueFromScope(null, args);
    }
}
