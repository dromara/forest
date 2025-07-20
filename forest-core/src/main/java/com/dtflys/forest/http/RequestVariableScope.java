package com.dtflys.forest.http;

import com.dtflys.forest.config.VariableScope;

public interface RequestVariableScope<SELF extends RequestVariableScope<SELF>> extends VariableScope<SELF> {

    ForestRequest asRequest();
}
