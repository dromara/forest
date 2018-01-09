package org.forest.mapping;

import org.apache.commons.collections.CollectionUtils;
import org.forest.config.ForestConfiguration;
import org.forest.config.VariableScope;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.filter.Filter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-08 21:03
 */
public class MappingFilterInvoke extends MappingInvoke {

    public MappingFilterInvoke(VariableScope variableScope, MappingIdentity name, List<MappingExpr> argList) {
        super(variableScope, null, name, argList);
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
