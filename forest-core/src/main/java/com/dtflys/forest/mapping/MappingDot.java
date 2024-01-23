package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author gongjun
 * @since 2016-06-12
 */
public class MappingDot extends MappingExpr {

    protected final MappingExpr left;
    protected final MappingIdentity right;

    public MappingDot(MappingExpr left, MappingIdentity right) {
        this(Token.DOT, left, right);
    }

    protected MappingDot(Token token, MappingExpr left, MappingIdentity right) {
        super(token);
        this.left = left;
        this.right = right;
    }

    public Method getPropMethodFromClass(Class clazz, MappingIdentity right) {
        Method method = null;
        String getterName = StringUtils.toGetterName(right.getName());
        Throwable th = null;
        try {
            method = clazz.getDeclaredMethod(getterName);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getDeclaredMethod(right.getName());
            } catch (NoSuchMethodException e1) {
                th = e1;
            }
        }
        if (method == null) {
            if (!Object.class.equals(clazz)) {
                return getPropMethodFromClass(clazz.getSuperclass(), right);
            }
            if (th != null) {
                throw new ForestRuntimeException(th);
            }
        }
        return method;
    }


    @Override
    public boolean isIterateVariable() {
        boolean ret = false;
        if (left != null) {
            ret = left.isIterateVariable();
        }
        if (right != null) {
            ret = ret || right.isIterateVariable();
        }
        return ret;
    }

    @Override
    public Object render(VariableValueContext valueContext) {
        Object obj = left.render(valueContext);
        if (obj == null) {
            throw new ForestRuntimeException(new NullPointerException());
        }
        if (obj instanceof Map) {
            return ((Map) obj).get(right.getName());
        }
        String getterName = StringUtils.toGetterName(right.getName());
        Method method = getPropMethodFromClass(obj.getClass(), right);
        if (method == null) {
            throw new ForestRuntimeException(new NoSuchMethodException(getterName));
        }
        try {
            Object result = method.invoke(obj);
            return result;
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
