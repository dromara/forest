package org.forest.mapping;

import org.apache.commons.collections.CollectionUtils;
import org.forest.config.VariableScope;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-08 18:21
 */
public class MappingMethod extends MappingDot {

    private List<MappingExpr> argList;

    public MappingMethod(VariableScope variableScope, MappingExpr left, String name, List<MappingExpr> argList) {
        super(variableScope, left, name);
        this.argList = argList;
    }

    public List<MappingExpr> getArgList() {
        return argList;
    }

    @Override
    public Object render(Object[] args) {
        Object obj = left.render(args);
        String methodName = right;
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName);
            Object result = null;
            if (CollectionUtils.isEmpty(argList)) {
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
}
