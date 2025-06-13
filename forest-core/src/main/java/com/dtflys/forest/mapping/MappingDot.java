package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.exceptions.*;
import com.dtflys.forest.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author gongjun
 * @since 2016-06-12
 */
public class MappingDot extends MappingExpr {

    protected final MappingExpr left;
    protected final MappingIdentity right;

    public MappingDot(MappingTemplate source, MappingExpr left, MappingIdentity right, int startIndex, int endIndex) {
        this(source, Token.DOT, left, right, startIndex, endIndex);
    }

    protected MappingDot(MappingTemplate source, Token token, MappingExpr left, MappingIdentity right, int startIndex, int endIndex) {
        super(source, token);
        this.left = left;
        this.right = right;
        setIndexRange(startIndex, endIndex);
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
                try {
                    return getPropMethodFromClass(clazz.getSuperclass(), right);
                } catch (Throwable ex) {
                    if (th != null) {
                        throw new ForestRuntimeException(th);
                    }
                }
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


    protected Object renderRight(Object obj) {
        if (obj instanceof Map) {
            return ((Map) obj).get(right.getName());
        }
        String getterName = StringUtils.toGetterName(right.getName());
        Method method = null;
        try {
            method = getPropMethodFromClass(obj.getClass(), right);
        } catch (Throwable th) {
        }
        if (method == null) {
            final String className = obj.getClass().getName();
            NoSuchMethodException ex = new NoSuchMethodException(getterName);
            throwExpressionException(
                    "No such property method: " + className + "." + right.getName() + "() or " + className + "." + getterName + "()",
                    right, ex);
        }
        try {
            return method.invoke(obj);
        } catch (Throwable th) {
            if (th instanceof NullPointerException) {
                throwExpressionNulException(null, right, th);
            } else {
                throwExpressionException(th.getMessage(), right, th);
            }
        }
        return null;
    }

    @Override
    public Object render(VariableScope scope, Object[] args) {
        Object obj = left.render(scope, args);
        if (obj == null) {
            throwExpressionNulException(left.toTemplateString(), right, new NullPointerException());
            return null;
        }
        if (obj == MappingEmpty.OPTIONAL) {
            return obj;
        }
        return checkDeepReference(renderRight(obj), this, scope, args);
    }


    @Override
    public String toString() {
        return "[Dot: " + left.toString() + "." + right + "]";
    }

}
