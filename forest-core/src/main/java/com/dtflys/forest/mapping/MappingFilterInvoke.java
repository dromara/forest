package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.filter.Filter;

import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.0.0
 */
public class MappingFilterInvoke extends MappingInvoke {

    public MappingFilterInvoke(MappingIdentity name, List<MappingExpr> argList) {
        super(Token.FILTER_INVOKE, null, name, argList);
    }

    @Override
    public Object render(VariableValueContext valueContext) {
        ForestConfiguration configuration = valueContext.getConfiguration();
        Filter filter = configuration.newFilterInstance(right.getName());
        List<MappingExpr> exprList = getArgList();
        Object[] invokeArgs = new Object[exprList.size()];
        for (int i = 0; i < exprList.size(); i++) {
            MappingExpr expr = exprList.get(i);
            Object renderedArg = expr.render(valueContext);
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
