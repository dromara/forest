package org.forest.mapping;

import org.forest.reflection.ForestMethod;

/**
 * Created by Administrator on 2016/5/26.
 */
public class MappingReference extends MappingExpr {

    private String name;

    public MappingReference(ForestMethod method, String name) {
        this.forestMethod = method;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object render(Object[] args) {
        MappingVariable variable = forestMethod.getVariable(name);
        if (variable != null) {
            return args[variable.getIndex()];
        }
        return forestMethod.getVariableValue(name);
    }

    @Override
    public String toString() {
        return "[Refer: " + name + "]";
    }
}
