package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestProperties;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;
import com.dtflys.forest.reflection.ForestMethod;

public class MappingProperty extends MappingExpr {

    private final String propertyName;
    private ForestProperties properties;

    protected MappingProperty(ForestMethod<?> forestMethod, String propertyName) {
        super(forestMethod, Token.PROP);
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
    public Object render(RequestVariableScope scope, Object[] args) {
        return scope.getConfiguration().getProperties().getProperty(propertyName, null);
    }
}
