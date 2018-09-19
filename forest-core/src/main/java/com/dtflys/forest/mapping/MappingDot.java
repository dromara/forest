package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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


    public Object render(Object[] args) {
        Object obj = left.render(args);
        String getterName = StringUtils.toGetterName(right.getName());
        Method method = null;
        try {
            method = obj.getClass().getDeclaredMethod(getterName);
        } catch (NoSuchMethodException e) {
            try {
                method = obj.getClass().getDeclaredMethod(right.getName());
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
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
