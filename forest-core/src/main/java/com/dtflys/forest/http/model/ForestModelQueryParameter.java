package com.dtflys.forest.http.model;

import com.dtflys.forest.http.AbstractQueryParameter;
import com.dtflys.forest.utils.StringUtils;

public class ForestModelQueryParameter extends AbstractQueryParameter<ForestModelQueryParameter> {

    private String alias;

    private final ForestModelProperty property;

    public ForestModelQueryParameter(ForestModelProperty property, String alias, boolean isFromUrl) {
        super(isFromUrl);
        this.property = property;
        this.alias = alias;
    }

    @Override
    public String getName() {
        if (StringUtils.isNotEmpty(alias)) {
            return alias;
        }
        return property.getName();
    }

    @Override
    public Object getValue() {
        return property.getValue();
    }

    @Override
    public ForestModelQueryParameter setValue(Object value) {
        property.setValue(value);
        return this;
    }

}
