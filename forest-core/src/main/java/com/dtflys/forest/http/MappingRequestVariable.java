package com.dtflys.forest.http;

import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.MappingURLTemplate;

public class MappingRequestVariable<T> {

    private ForestRequest<?> request;

    private MappingTemplate template;

    private T value;

    public MappingRequestVariable(ForestRequest<?> request) {
        this.request = request;
    }

    public ForestRequest<?> getRequest() {
        return request;
    }

    public void setRequest(ForestRequest<?> request) {
        this.request = request;
    }

    public MappingTemplate getTemplate() {
        return template;
    }

    public void setTemplate(MappingTemplate template) {
        this.template = template;
    }

    public void setTemplate(String template) {
        this.template = MappingTemplate.text(template);
    }

    public void setURLTemplate(String template) {
        this.template = MappingURLTemplate.text(template);
    }


    public T getValue() {
        if (value == null) {
            return (T) template.render(request);
        }
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
