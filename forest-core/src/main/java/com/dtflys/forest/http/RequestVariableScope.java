package com.dtflys.forest.http;

import com.dtflys.forest.config.VariableScope;

public interface RequestVariableScope extends VariableScope {

    ForestRequest asRequest();
}
