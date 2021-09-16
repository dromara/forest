package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.Success;
import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;

/**
 * 重试注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class SuccessLifeCycle implements MethodAnnotationLifeCycle<Success, Object> {

    @Override
    public void onMethodInitialized(ForestMethod method, Success annotation) {
        method.setExtensionParameterValue("successAnnotation", annotation);
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Success annotation = (Success) request.getMethod().getExtensionParameterValue("successAnnotation");
        Class<? extends SuccessWhen> conditionClass = annotation.condition();
        if (conditionClass != null && !SuccessWhen.class.equals(conditionClass)) {
            request.successWhen(conditionClass);
        }
    }


}
