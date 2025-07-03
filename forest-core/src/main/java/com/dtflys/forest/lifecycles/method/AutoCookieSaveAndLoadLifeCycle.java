package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.AutoCookieSaveAndLoad;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;

public class AutoCookieSaveAndLoadLifeCycle implements MethodAnnotationLifeCycle<AutoCookieSaveAndLoad, Void> {

    @Override
    public void onMethodInitialized(ForestMethod method, AutoCookieSaveAndLoad annotation) {

    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        Boolean value = getAttributeAsBoolean(request, "value");
        request.autoCookieSaveAndLoadEnabled(value);
        return true;
    }

}
