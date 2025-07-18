package com.dtflys.forest.http;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ConstantVariable;
import com.dtflys.forest.reflection.ForestVariable;
import com.dtflys.forest.reflection.TemplateVariable;

public class SimpleHeader implements ForestHeader<SimpleHeader, String> {

    protected final HasURL hasURL;

    /**
     * 请求头名称
     */
    protected final String name;

    /**
     * 请求头的值
     */
    private ForestVariable value;

    public SimpleHeader(HasURL hasURL, String name, ForestVariable value) {
        this.hasURL = hasURL;
        this.name = name;
        this.value = value;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        if (value == null) {
            return null;
        }
        if (value instanceof ConstantVariable) {
            final Object val =  ((ConstantVariable) value).getValue();
            if (val == null) {
                return null;
            }
            return String.valueOf(val);
        }
        if (value instanceof TemplateVariable) {
            MappingTemplate template = ((TemplateVariable) value).getTemplate();
            if (template.isConstant()) {
                return template.getPureTextConstant();
            }
        }
        if (!(hasURL instanceof ForestRequest || hasURL instanceof ForestResponse)) {
            throw new ForestRuntimeException(
                    "the request of header[name=" + name + "] dose not exist");
        }
        final ForestRequest request = hasURL instanceof ForestRequest ?
                (ForestRequest) hasURL : null;
        return ForestVariable.getStringValue(value, request);
    }

    @Override
    public SimpleHeader setValue(String value) {
        this.value = ForestVariable.create(value);
        return this;
    }

    @Override
    public SimpleHeader clone(ForestHeaderMap headerMap) {
        return new SimpleHeader(headerMap.hasURL, name, value);
    }
}
