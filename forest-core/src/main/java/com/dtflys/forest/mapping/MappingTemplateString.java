package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;

public class MappingTemplateString extends MappingExpr {

    private final MappingTemplate template;

    protected MappingTemplateString(MappingTemplate source, String template, int startIndex, int endIndex) {
        this(source, MappingTemplate.create(source.forestMethod, template, startIndex, endIndex), startIndex, endIndex);
    }


    protected MappingTemplateString(MappingTemplate source, MappingTemplate template, int startIndex, int endIndex) {
        super(source, Token.STRING);
        this.template = template;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public Object render(VariableScope scope, Object[] args) {
        if (template == null) {
            return null;
        }
        return template.render(scope, args);
    }

    @Override
    public boolean isIterateVariable() {
        return false;
    }

    public MappingTemplate getTemplate() {
        return template;
    }
}
