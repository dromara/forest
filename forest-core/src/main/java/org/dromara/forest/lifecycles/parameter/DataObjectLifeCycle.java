package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.annotation.DataObject;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.lifecycles.ParameterAnnotationLifeCycle;
import org.dromara.forest.utils.StringUtils;

/**
 * Forest &#064;DataObject注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 2:15
 */
public class DataObjectLifeCycle implements ParameterAnnotationLifeCycle<DataObject, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, DataObject annotation) {
        String jsonParamName = annotation.jsonParam();
        String filterName = annotation.filter();
        boolean isJsonParam = StringUtils.isNotEmpty(jsonParamName);
        parameter.setObjectProperties(true);
        parameter.setJsonParam(isJsonParam);
        if (isJsonParam) {
            parameter.setJsonParamName(jsonParamName);
        }
        method.processParameterFilter(parameter, filterName);
        parameter.setTarget(MappingParameter.TARGET_UNKNOWN);
        method.addNamedParameter(parameter);
    }
}
