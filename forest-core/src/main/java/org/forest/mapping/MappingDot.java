package org.forest.mapping;

import org.forest.reflection.ForestMethod;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author gongjun
 * @since 2016-06-12
 */
public class MappingDot extends MappingExpr {

    private MappingExpr left;
    private String right;

    public MappingDot(ForestMethod method, MappingExpr left, String right) {
        this.forestMethod = method;
        this.left = left;
        this.right = right;
    }

    public Object render(Object[] args) {
        Object obj = left.render(args);
        String getterName = StringUtils.toGetterName(right);
        try {
            Method method = obj.getClass().getDeclaredMethod(getterName);
            Object result = method.invoke(obj);
            return result;
        } catch (NoSuchMethodException e) {
            throw new ForestRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ForestRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "[Dot: " + left.toString() + "." + right + "]";
    }
}
