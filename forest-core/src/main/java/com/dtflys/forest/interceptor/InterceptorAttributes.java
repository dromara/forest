package com.dtflys.forest.interceptor;

import com.dtflys.forest.mapping.MappingTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 拦截器属性类，用于封装通过注解传入的拦截器属性
 */
public class InterceptorAttributes {

    private final Class interceptorClass;

    private final Map<String, Object> attributeTemplates;

    private Map<String, Object> attributes = new HashMap();

    public InterceptorAttributes(Class interceptorClass, Map<String, Object> attributeTemplates) {
        this.interceptorClass = interceptorClass;
        this.attributeTemplates = attributeTemplates;
    }

    public void addAttributeTemplate(String attributeName, Object template) {
        attributeTemplates.put(attributeName, template);
    }

    public Map<String, Object> render(Object[] args) {
        for (Map.Entry<String, Object> entry : attributeTemplates.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof MappingTemplate) {
                value = ((MappingTemplate) value).render(args);
            }
            attributes.put(name, value);
        }
        return attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    public void addAttribute(String attributeName, Object attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

    public Class getInterceptorClass() {
        return interceptorClass;
    }
}
