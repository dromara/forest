package com.dtflys.forest.reflection;

import com.dtflys.forest.Forest;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.MappingValue;
import com.dtflys.forest.mapping.MappingVariable;

@FunctionalInterface
public interface ForestVariable {

    static ForestVariable create(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof ForestVariable) {
            return (ForestVariable) value;
        } else if (value instanceof CharSequence) {
            final MappingTemplate mappingTemplate = MappingTemplate.create(String.valueOf(value));
            if (mappingTemplate.isConstant()) {
                return new ConstantVariable(mappingTemplate.getPureTextConstant());
            } else {
                return new TemplateVariable(mappingTemplate);
            }
        } else if (value instanceof MappingTemplate) {
            return new TemplateVariable((MappingTemplate) value);
        } else {
            return new BasicVariable(value);
        }
    }

    static <R> R getValue(ForestVariable variable, ForestRequest req, Class<R> clazz) {
        if (variable == null) {
            return null;
        }
        return variable.getValue(req, clazz);
    }

    static Object getValue(ForestVariable variable, ForestRequest req) {
        if (variable == null) {
            return null;
        }
        return variable.getValue(req);
    }

    static String getStringValue(ForestVariable variable, ForestRequest req) {
        return getValue(variable, req, String.class);
    }

    static Integer getIntegerValue(ForestVariable variable, ForestRequest req) {
        return getValue(variable, req, Integer.class);
    }



    Object getValue(ForestRequest req);
    
    
    default  <R> R getValue(ForestRequest req, Class<R> clazz) {
        Object value = getValue(req);
        if (value == null) {
            return null;
        }
        if (!(this instanceof ConstantVariable)) {
            if (value instanceof ForestVariable) {
                return getValue((ForestVariable) value, req, clazz);
            }
            if (value instanceof CharSequence) {
                value = MappingTemplate.create(req, String.valueOf(value));
            }
            if (value instanceof MappingTemplate) {
                final MappingTemplate template = (MappingTemplate) value;
                if (template.isConstant() || req == null) {
                    value = template.getPureTextConstant();
                } else {
                    value = template.render(req, req.arguments());
                }
            }
        }
        if (clazz != null) {
            if (clazz.isAssignableFrom(value.getClass())) {
                return clazz.cast(value);
            }
            if (int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
                return clazz.cast(Integer.parseInt(String.valueOf(value)));
            }
            if (long.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)) {
                return clazz.cast(Long.parseLong(String.valueOf(value)));
            }
            if (req != null) {
                return req.getConfiguration().getJsonConverter().convertToJavaObject(String.valueOf(value), clazz);
            }
            return Forest.config().getConfiguration().getJsonConverter().convertToJavaObject(String.valueOf(value), clazz);
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
