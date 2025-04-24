package com.dtflys.forest.reflection;

import com.dtflys.forest.http.ForestRequest;

/**
 * Forest 变量接口
 */
@FunctionalInterface
public interface ForestMethodVariable extends ForestArgumentsVariable {
    
    default Object getValue(ForestRequest request, Object[] args) {
        return getValue(request.getMethod());
    }

    Object getValue(ForestMethod method);

}
