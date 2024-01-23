package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestProperties;
import com.dtflys.forest.config.VariableValueContext;

public class MappingProperty extends MappingExpr {

    private final String propertyName;
    private ForestProperties properties;

    protected MappingProperty(String propertyName) {
        super(Token.PROP);
        this.propertyName = propertyName;
    }

    @Override
    public boolean isIterateVariable() {
        return false;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public ForestProperties getProperties() {
        return properties;
    }

    public void setProperties(ForestProperties properties) {
        this.properties = properties;
    }

    @Override
    public Object render(VariableValueContext valueContext) {
        return valueContext.getConfiguration().getProperties().getProperty(propertyName, null);
    }
}
