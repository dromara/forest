package com.dtflys.forest.reflection;

import com.dtflys.forest.http.ForestRequest;

@FunctionalInterface
public interface ForestArgumentsVariable extends ForestVariable {
    
    default Object getValue(ForestRequest req) {
        return getValue(req, req != null ? req.arguments() : new Object[0]);
    }
    
    Object getValue(ForestRequest req, Object[] args);
}
