package org.dromara.forest.utils;

import java.lang.reflect.Type;

/**
 * {@link Type} 泛型参数类型引用工具类
 * <p>用于获取带泛型的类型 {@link Type} 接口实例
 *
 * 该工具类使用方式如下:
 * <pre>
 *     Type type = new TypeReference&lt;Map&lt;String, Integer&gt;&gt;() {}.getType();
 * </pre>
 * 这里的 type 即为 Map&lt;String, Integer&gt;
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.2
 */
public abstract class TypeReference<T> implements Type {

    /**
     * {@link Type} 类型引用
     * <p>从泛型参数中获取的 {@link Type} 接口实例
     */
    private final Type type = ReflectUtils.getGenericArgument(this.getClass());


    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
