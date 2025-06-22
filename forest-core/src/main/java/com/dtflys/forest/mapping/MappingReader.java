package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.converter.json.ForestJsonConverter;


public abstract class MappingReader<T> {

    private final MappingTemplate template;

    private volatile T value;

    private volatile boolean rendered = false;

    public MappingReader(String source) {
        this.template = MappingTemplate.create(source);
    }

    public MappingReader(MappingTemplate template) {
        this.template = template;
    }

    public MappingTemplate getTemplate() {
        return template;
    }

    protected T getValue(VariableScope scope, Object[] arguments, Class<T> type) {
        if (value != null) {
            return value;
        }
        if (!rendered) {
            final ForestConfiguration configuration = scope.getConfiguration();
            final ForestJsonConverter converter = configuration.getJsonConverter();
            final String val = template.render(scope, arguments);
            if (CharSequence.class.equals(type)) {
                this.value = (T) val;
            } else {
                this.value = converter.convertToJavaObject(val, type);
            }
            rendered = true;
        }
        return value;
    }


    public abstract T getValue(VariableScope scope, Object[] arguments);

    public void setValue(T value) {
        this.value = value;
    }
}
