package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.filter.Filter;

import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-08 21:03
 */
public class MappingFilterInvoke extends MappingInvoke {

    public MappingFilterInvoke(VariableScope variableScope, MappingIdentity name, List<MappingExpr> argList) {
        super(Token.FILTER_INVOKE, variableScope, null, name, argList);
    }

    @Override
    public Object render(Object[] args) {
        ForestConfiguration configuration = variableScope.getConfiguration();
        Filter filter = configuration.newFilterInstance(right.getName());
        return filter.doFilter(configuration, args[0]);
    }

    @Override
    public String toString() {
        return "[FInvoke: " + right + " ()]";
    }
}
