package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-08 18:21
 */
public class MappingInvoke extends MappingDot {

    private List<MappingExpr> argList;

    public MappingInvoke(VariableScope variableScope, MappingExpr left, MappingIdentity name, List<MappingExpr> argList) {
        this(Token.INVOKE, variableScope, left, name, argList);
    }

    protected MappingInvoke(Token token, VariableScope variableScope, MappingExpr left, MappingIdentity name, List<MappingExpr> argList) {
        super(token, variableScope, left, name);
        this.argList = argList;
    }

    public List<MappingExpr> getArgList() {
        return argList;
    }

    @Override
    public void setVariableScope(VariableScope variableScope) {
        super.setVariableScope(variableScope);
        if (argList != null) {
            for (MappingExpr arg : argList) {
                arg.setVariableScope(variableScope);
            }
        }
    }

    @Override
    public Object render(Object[] args) {
        Object obj = left.render(args);
        String methodName = right.getName();
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName);
            Object result = null;
            if (argList == null || argList.isEmpty()) {
                result = method.invoke(obj);
            }
            else {
                Object[] renderArgs = new Object[argList.size()];
                for (int i = 0, len = argList.size(); i < len; i++) {
                    MappingExpr expr = argList.get(i);
                    renderArgs[i] = expr.render(args);
                }
                result = method.invoke(obj, renderArgs);
            }
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
        return "[Invoke: " + left.toString() + "." + right + " ()]";
    }
}
