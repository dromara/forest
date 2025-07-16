package com.dtflys.forest.reflection;

import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingTemplate;

public class BasicVariable implements ForestVariable {

    protected final Object value;

    public BasicVariable(Object value) {
        this.value = value;
    }

    @Override
    public Object getOriginalValue() {
        return value;
    }

    @Override
    public Object getValue(ForestRequest request) {
        if (value instanceof MappingTemplate) {
            return ((MappingTemplate) value).render(
                    request == null ? Forest.config() : request,
                    request == null ? null : request.arguments());
        }
        return value;
    }
    
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
