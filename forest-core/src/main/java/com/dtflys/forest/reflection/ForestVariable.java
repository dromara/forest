package com.dtflys.forest.reflection;

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
    
}
