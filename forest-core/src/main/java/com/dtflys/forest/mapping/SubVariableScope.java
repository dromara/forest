package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.BasicVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 子变量作用域
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public class SubVariableScope extends AbstractVariableScope implements VariableScope {

    public SubVariableScope(VariableScope parent) {
        super(parent);
    }

    @Override
    public ForestConfiguration getConfiguration() {
        return parent.getConfiguration();
    }
}
