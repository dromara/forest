package com.dtflys.forest.reflection;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.MappingValue;

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

    default  Object getValueFromScope(VariableScope scope, boolean deepReference) {
        if (!deepReference && this instanceof TemplateVariable) {
            final MappingTemplate template = ((TemplateVariable) this).getTemplate();
            if (template != null) {
                return MappingValue.rendered(template.getSource());
            }
        }
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
