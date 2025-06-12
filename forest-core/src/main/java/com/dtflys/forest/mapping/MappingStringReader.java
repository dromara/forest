package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;

public class MappingStringReader extends MappingReader<String> {

    public MappingStringReader(String source) {
        super(source);
    }

    public MappingStringReader(MappingTemplate template) {
        super(template);
    }

    @Override
    public String getValue(VariableScope scope, Object[] arguments) {
        return getValue(scope, arguments, String.class);
    }
}
