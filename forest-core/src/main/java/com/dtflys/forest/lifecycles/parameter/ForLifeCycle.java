package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.For;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.reflection.ForestMethod;

import java.util.Iterator;

/**
 * Forest &#064;For注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 2:17
 */
public class ForLifeCycle implements ParameterAnnotationLifeCycle<For, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, For annotation) {
        String name = annotation.value();
        String value = annotation.index();
        Iterator iterator = new Iterator() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }
        };
    }


}
