package org.forest.mapping;

import org.forest.reflection.ForestMethod;

/**
 * @author gongjun
 * @since 2016-05-04
 */
public abstract class MappingExpr {

    protected ForestMethod forestMethod;

    public Object render(Object[] args) {
        return null;
    }
}
