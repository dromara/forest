package org.dromara.forest.reflection;

import org.dromara.forest.utils.NameUtils;
import org.dromara.forest.utils.ReflectUtils;
import org.dromara.forest.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypeWrapper {

    private final static Map<Class<?>, TypeWrapper> TYPE_CACHE = new ConcurrentHashMap<>();

    private final Class<?> clazz;

    private final Map<String, PropWrapper> props = new LinkedHashMap<>();

    public static TypeWrapper get(final Class<?> clazz) {
        TypeWrapper typeWrapper = TYPE_CACHE.get(clazz);
        if (typeWrapper == null) {
            typeWrapper = new TypeWrapper(clazz);
            TYPE_CACHE.put(clazz, typeWrapper);
        }
        return typeWrapper;
    }


    private TypeWrapper(final Class<?> clazz) {
        this.clazz = clazz;
        init();
    }

    private void init() {
        final Method[] methods = ReflectUtils.getMethods(clazz);
        final Map<String, PropWrapper> newProps = new LinkedHashMap<>();
        for (final Method method : methods) {
            String methodName = method.getName();
            int paramCount = method.getParameterCount();
            if (paramCount == 0 && NameUtils.isGetter(methodName)) {
                final String name = NameUtils.propNameFromGetter(methodName);
                if (StringUtils.isNotEmpty(name)) {
                    PropWrapper prop = newProps.get(name);
                    if (prop == null) {
                        prop = new PropWrapper(name);
                        newProps.put(name, prop);
                    }
                    prop.getter = method;
                }
            } else if (paramCount == 1 && NameUtils.isSetter(methodName)) {
                final String name = NameUtils.propNameFromSetter(methodName);
                if (StringUtils.isNotEmpty(name)) {
                    PropWrapper prop = newProps.get(name);
                    if (prop == null) {
                        prop = new PropWrapper(name);
                        newProps.put(name, prop);
                    }
                    prop.setter = method;
                }
            }
        }

        final Field[] fields = ReflectUtils.getFields(clazz);
        for (final Field field : fields) {
            if (!field.isAccessible()) {
                continue;
            }
            final String name = field.getName();
            PropWrapper prop = newProps.get(name);
            if (prop == null) {
                prop = new PropWrapper(name);
                newProps.put(name, prop);
            }
            prop.field = field;
        }

        newProps.values().stream()
                .filter(prop -> prop.getter != null)
                .forEach(prop -> this.props.put(prop.getName(), prop));
    }

    public Class<?> getJavaType() {
        return clazz;
    }

    public Map<String, PropWrapper> getProps() {
        return props;
    }

    public PropWrapper getProp(String name) {
        return props.get(name);
    }

}
