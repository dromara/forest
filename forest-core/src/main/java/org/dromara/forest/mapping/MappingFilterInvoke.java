package org.dromara.forest.mapping;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.config.VariableScope;
import org.dromara.forest.filter.Filter;
import org.dromara.forest.reflection.ForestMethod;

import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.0.0
 */
public class MappingFilterInvoke extends MappingInvoke {

    public MappingFilterInvoke(ForestMethod<?> forestMethod, VariableScope variableScope, MappingIdentity name, List<MappingExpr> argList) {
        super(forestMethod, Token.FILTER_INVOKE, variableScope, null, name, argList);
    }

    @Override
    public Object render(Object[] args) {
        ForestConfiguration configuration = variableScope.getConfiguration();
        Filter filter = configuration.newFilterInstance(right.getName());
        List<MappingExpr> exprList = getArgList();
        Object[] invokeArgs = new Object[exprList.size()];
        for (int i = 0; i < exprList.size(); i++) {
            MappingExpr expr = exprList.get(i);
            Object renderedArg = expr.render(args);
            invokeArgs[i] = renderedArg;
        }
        if (invokeArgs.length > 0) {
            return filter.doFilter(configuration, invokeArgs[0]);
        }
        return null;
    }

    @Override
    public String toString() {
        return "[FInvoke: " + right + " ()]";
    }
}
