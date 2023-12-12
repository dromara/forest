package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 抽象请求体生命周期处理类
 *
 * @param <A> 参数注解类型
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public abstract class AbstractBodyLifeCycle<A extends Annotation> implements ParameterAnnotationLifeCycle<A> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, A annotation) {
        final Map<String, Object> attrs = ReflectUtils.getAttributesFromAnnotation(annotation);
        final String name = (String) attrs.get("name");
        final String filterName = (String) attrs.get("filter");
        final String partContentType = (String) attrs.get("partContentType");
        final String defaultValue = (String) attrs.get("defaultValue");
        if (StringUtils.isNotEmpty(name)) {
            parameter.setName(name);
            final MappingVariable variable = new MappingVariable(name, parameter.getType());
            variable.setIndex(parameter.getIndex());
            method.addVariable(name, variable);
            parameter.setObjectProperties(false);
        } else {
            parameter.setObjectProperties(true);
        }
        if (StringUtils.isNotBlank(partContentType)) {
            parameter.setPartContentType(partContentType.trim());
        }
        if (StringUtils.isNotEmpty(defaultValue)) {
            parameter.setDefaultValue(defaultValue);
        }
        method.processParameterFilter(parameter, filterName);
        parameter.setTarget(MappingParameter.TARGET_BODY);
        method.addNamedParameter(parameter);
    }
}
