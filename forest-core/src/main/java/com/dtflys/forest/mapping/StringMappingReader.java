package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;

public class StringMappingReader extends MappingReader<String> {

    public StringMappingReader(String source) {
        super(source);
    }

    public StringMappingReader(MappingTemplate template) {
        super(template);
    }

    @Override
    public String getValue(VariableScope scope, Object[] arguments) {
        return super.getValue(scope, arguments, String.class);
    }
}
