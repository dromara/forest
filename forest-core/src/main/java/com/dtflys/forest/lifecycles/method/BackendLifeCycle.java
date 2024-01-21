package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.Backend;
import com.dtflys.forest.annotation.Success;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

/**
 * Forest后端框架注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class BackendLifeCycle implements MethodAnnotationLifeCycle<Backend> {

    private final static String PARAM_KEY_BACKEND_NAME = "__backend_name";

    @Override
    public void onMethodInitialized(ForestMethod method, Backend annotation) {
        final String backendName = annotation.value();
        if (StringUtils.isNotBlank(backendName)) {
            final MappingTemplate template = method.makeTemplate(Backend.class, "value", backendName);
            method.setExtensionParameterValue(PARAM_KEY_BACKEND_NAME, template);
        }
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Object backendName = request.getMethod().getExtensionParameterValue(PARAM_KEY_BACKEND_NAME);
        if (backendName != null && backendName instanceof MappingTemplate) {
            request.setBackend(((MappingTemplate) backendName).render(request, args));
        }
    }


}
