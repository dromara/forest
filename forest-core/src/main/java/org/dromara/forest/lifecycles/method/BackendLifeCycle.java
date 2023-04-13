package org.dromara.forest.lifecycles.method;

import org.dromara.forest.annotation.Backend;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.mapping.MappingTemplate;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.utils.StringUtils;

/**
 * Forest后端框架注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class BackendLifeCycle implements MethodAnnotationLifeCycle<Backend, Object> {

    private final static String PARAM_KEY_BACKEND_NAME = "__backend_name";

    @Override
    public void onMethodInitialized(ForestMethod method, Backend annotation) {
        String backendName = annotation.value();
        if (StringUtils.isNotBlank(backendName)) {
            MappingTemplate template = method.makeTemplate(Backend.class, "value", backendName);
            method.setExtensionParameterValue(PARAM_KEY_BACKEND_NAME, template);
        }
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Object backendName = request.getMethod().getExtensionParameterValue(PARAM_KEY_BACKEND_NAME);
        if (backendName != null && backendName instanceof MappingTemplate) {
            request.setBackend(((MappingTemplate) backendName).render(args));
        }
    }


}
