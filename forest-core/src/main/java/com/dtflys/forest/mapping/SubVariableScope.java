package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;

/**
 * 子变量作用域
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public class SubVariableScope extends AbstractVariableScope implements RequestVariableScope {

    public SubVariableScope(VariableScope parent) {
        super(parent);
    }

    @Override
    public ForestConfiguration getConfiguration() {
        return parent.getConfiguration();
    }

    @Override
    public ForestRequest asRequest() {
        if (parent != null && parent instanceof RequestVariableScope) {
            return ((RequestVariableScope) parent).asRequest();
        }
        return null;

    }
}
