package org.dromara.forest.http.model;

import org.dromara.forest.reflection.TypeWrapper;

import java.util.HashMap;
import java.util.Map;

public class ObjectWrapper {

    private final Object instance;

    private final TypeWrapper typeWrapper;

    private final Map<String, ObjectProperty> properties = new HashMap<>();

    public ObjectWrapper(Object instance) {
        this.instance = instance;
        this.typeWrapper = TypeWrapper.get(instance.getClass());
        this.typeWrapper.getProps().values().forEach(prop ->
            properties.put(prop.getName(), new JavaObjectProperty(instance, prop.getName(), prop))
        );
    }

    public Object getInstance() {
        return instance;
    }

    public Map<String, ObjectProperty> getProperties() {
        return properties;
    }

    public ObjectProperty getProperty(final String name) {
        return properties.get(name);
    }

}
