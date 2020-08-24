package com.dtflys.forest.converter.json;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.ForestEncoder;

import java.util.Map;

/**
 * Json消息转换接口
 * @author gongjun
 * @since 2016-05-30
 */
public interface ForestJsonConverter extends ForestConverter<String>, ForestEncoder {

    Map<String, Object> convertObjectToMap(Object obj);
}
