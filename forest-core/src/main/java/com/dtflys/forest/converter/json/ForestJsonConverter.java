package com.dtflys.forest.converter.json;

import com.dtflys.forest.converter.ForestConverter;

/**
 * Json消息转换接口
 * @author gongjun
 * @since 2016-05-30
 */
public interface ForestJsonConverter extends ForestConverter {

    /**
     * 将Java对象转换为JSON字符串
     * @param obj
     * @return
     */
    String convertToJson(Object obj);

}
