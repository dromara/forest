package com.dtflys.forest.reflection;

import com.dtflys.forest.Forest;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingTemplate;

public class TemplateVariable implements ForestVariable {

    private MappingTemplate template;

    public TemplateVariable(MappingTemplate template) {
        this.template = template;
    }
    
    @Override
    public Object getValue(ForestRequest req) {
        return template.render(req == null ? Forest.config() : req, req == null ? null : req.arguments());
    }
    
    public Object getValue(VariableScope scope) {
        return template.render(scope, null);
    }
    
    public void setValue(MappingTemplate template) {
        this.template = template;
    }
    
    public void setValue(ForestRequest req, String value) {
        this.template = MappingTemplate.create(req, value);
    }
    
    public MappingTemplate getTemplate() {
        return template;
    }
}
