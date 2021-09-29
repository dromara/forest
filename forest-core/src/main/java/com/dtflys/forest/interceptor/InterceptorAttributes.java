package com.dtflys.forest.interceptor;

import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拦截器属性类，用于封装通过注解传入的拦截器属性
 */
public class InterceptorAttributes {

    private final Class interceptorClass;

    private final Map<String, Object> attributeTemplates;

    private Map<String, Object> attributes = new ConcurrentHashMap<>();

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
            } else if (value instanceof MappingTemplate[]) {
                MappingTemplate[] templates = (MappingTemplate[]) value;
                int len = templates.length;
                String[] strArray = new String[len];
                for (int i = 0; i < len; i++) {
                    strArray[i] = templates[i].render(args);
                }
                value = strArray;
            }
            attributes.put(name, value);
        }
        return attributes;
    }

    public Map<String, Object> getAttributeTemplates() {
        return attributeTemplates;
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
