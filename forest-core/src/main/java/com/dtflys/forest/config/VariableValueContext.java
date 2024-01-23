package com.dtflys.forest.config;

import com.dtflys.forest.http.ForestQueryMap;

public interface VariableValueContext extends VariableScope {

    Object[] getArguments();

    Object getArgument(int index);

    ForestQueryMap getQuery();

}
