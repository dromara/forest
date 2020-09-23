package com.dtflys.forest.converter.json;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.ForestEncoder;

import java.util.Map;

/**
 * Forest的JSON数据转换接口
 *
 * @author gongjun
 * @since 2016-05-30
 */
public interface ForestJsonConverter extends ForestConverter<String>, ForestEncoder {

    /**
     * 将源对象转换为Map对象
     *
     * @param obj  源对象
     * @return
     */
    Map<String, Object> convertObjectToMap(Object obj);
}
