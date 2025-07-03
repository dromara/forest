package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestProperties;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

public class MappingProperty extends MappingExpr {

    private final String propertyName;
    private ForestProperties properties;

    protected MappingProperty(MappingTemplate source, String propertyName, int startIndex, int endIndex) {
        super(source, Token.PROP);
        this.propertyName = propertyName;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
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
    public Object render(VariableScope scope, Object[] args) {
        String propValue = scope.getConfiguration().getProperties().getProperty(propertyName, null);
        if (StringUtils.isEmpty(propValue) || propValue.length() < 3) {
            return propValue;
        }
        return checkDeepReference(propValue, this, scope, args);
    }
}
