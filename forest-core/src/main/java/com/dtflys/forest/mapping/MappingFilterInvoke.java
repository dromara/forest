package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.filter.Filter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.ForestMethod;

import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.0.0
 */
public class MappingFilterInvoke extends MappingInvoke {

    public MappingFilterInvoke(ForestMethod<?> forestMethod, VariableScope variableScope, MappingIdentity name, List<MappingExpr> argList, int startIndex, int endIndex) {
        super(forestMethod, Token.FILTER_INVOKE, variableScope, null, name, argList, startIndex, endIndex);
    }

    @Override
    public Object render(ForestRequest request, Object[] args) {
        ForestConfiguration configuration = variableScope.getConfiguration();
        Filter filter = configuration.newFilterInstance(right.getName());
        List<MappingExpr> exprList = getArgList();
        Object[] invokeArgs = new Object[exprList.size()];
        for (int i = 0; i < exprList.size(); i++) {
            MappingExpr expr = exprList.get(i);
            Object renderedArg = expr.render(request, args);
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
