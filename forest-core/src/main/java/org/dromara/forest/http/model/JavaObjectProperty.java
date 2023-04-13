package org.dromara.forest.http.model;

import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.reflection.PropWrapper;

import java.lang.reflect.Method;

public class JavaObjectProperty implements ObjectProperty {

    private final Object instance;

    private final PropWrapper propWrapper;


    public JavaObjectProperty(Object instance, String name, PropWrapper propWrapper) {
        this.instance = instance;
        this.propWrapper = propWrapper;
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return propWrapper.getName();
    }


    public Method getGetter() {
        return propWrapper.getGetter();
    }

    public Method getSetter() {
        return propWrapper.getSetter();
    }

    @Override
    public Object getValue(ForestRequest request, ConvertOptions options) {
        Object value = this.propWrapper.getValue(this.instance);
        if (value == null) {
            return null;
        }
        return options.getValue(value, request);
    }




    public void setValue(Object value) {
        this.propWrapper.setValue(this.instance, value);
    }
}
