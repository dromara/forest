package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
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

    public MappingDot(VariableScope variableScope, MappingExpr left, MappingIdentity right) {
        this(Token.DOT, variableScope, left, right);
    }

    protected MappingDot(Token token, VariableScope variableScope, MappingExpr left, MappingIdentity right) {
        super(token);
        this.variableScope = variableScope;
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
    public void setVariableScope(VariableScope variableScope) {
        super.setVariableScope(variableScope);
        if (left != null) {
            left.setVariableScope(variableScope);
        }
        if (right != null) {
            right.setVariableScope(variableScope);
        }
    }

    @Override
    public Object render(Object[] args) {
        Object obj = left.render(args);
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
