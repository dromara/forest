package com.dtflys.forest.reflection;

import com.dtflys.forest.http.ForestRequest;

@FunctionalInterface
public interface ForestVariable {
    
    Object getValue(ForestRequest request);
    
    
    default  <R> R getValue(ForestRequest request, Class<R> clazz) {
        Object value = getValue(request);
        if (value == null) {
            return null;
        }
        if (clazz != null) {
            return clazz.cast(value);
        }
        return (R) value;
    }
    
}
