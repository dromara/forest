package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.lifecycles.ParameterAnnotationLifeCycle;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.mapping.MappingVariable;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.utils.ReflectUtil;
import org.dromara.forest.utils.StringUtil;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 抽象请求体生命周期处理类
 *
 * @param <A> 参数注解类型
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public abstract class AbstractBodyLifeCycle<A extends Annotation> implements ParameterAnnotationLifeCycle<A, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, A annotation) {
        Map<String, Object> attrs = ReflectUtil.getAttributesFromAnnotation(annotation);
        String name = (String) attrs.get("name");
        String filterName = (String) attrs.get("filter");
        String partContentType = (String) attrs.get("partContentType");
        String defaultValue = (String) attrs.get("defaultValue");
        if (StringUtil.isNotEmpty(name)) {
            parameter.setName(name);
            MappingVariable variable = new MappingVariable(name, parameter.getType());
            variable.setIndex(parameter.getIndex());
            method.addVariable(name, variable);
            parameter.setObjectProperties(false);
        } else {
            parameter.setObjectProperties(true);
        }
        if (StringUtil.isNotBlank(partContentType)) {
            parameter.setPartContentType(partContentType.trim());
        }
        if (StringUtil.isNotEmpty(defaultValue)) {
            parameter.setDefaultValue(defaultValue);
        }
        method.processParameterFilter(parameter, filterName);
        parameter.setTarget(MappingParameter.TARGET_BODY);
        method.addNamedParameter(parameter);

    }
}
