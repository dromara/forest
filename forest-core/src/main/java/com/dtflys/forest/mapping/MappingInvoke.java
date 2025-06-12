package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.reflection.ForestMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-08 18:21
 */
public class MappingInvoke extends MappingDot {

    private List<MappingExpr> argList;

    public MappingInvoke(MappingTemplate source, MappingExpr left, MappingIdentity name, List<MappingExpr> argList, int startIndex, int endIndex) {
        this(source, Token.INVOKE, left, name, argList, startIndex, endIndex);
    }

    protected MappingInvoke(MappingTemplate source, Token token, MappingExpr left, MappingIdentity name, List<MappingExpr> argList, int startIndex, int endIndex) {
        super(source, token, left, name, startIndex, endIndex);
        this.argList = argList;
    }

    public List<MappingExpr> getArgList() {
        return argList;
    }


    @Override
    public Object render(VariableScope scope, Object[] args) {
        Object obj = left.render(scope, args);
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
                    renderArgs[i] = expr.render(scope, args);
                }
                result = method.invoke(obj, renderArgs);
            }
            return result;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throwExpressionException(e.getMessage(), null);
        } catch (Throwable e) {
            throwExpressionException(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean isIterateVariable() {
        boolean ret = super.isIterateVariable();
        if (ret) {
            return true;
        }
        for (MappingExpr argExpr : argList) {
            if (argExpr.isIterateVariable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "[Invoke: " + left.toString() + "." + right + " ()]";
    }
}
