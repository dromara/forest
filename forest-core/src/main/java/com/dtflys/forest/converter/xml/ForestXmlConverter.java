package com.dtflys.forest.converter.xml;

import com.dtflys.forest.converter.ForestConverter;

/**
 * Xml消息转化接口
 * @author gongjun
 * @since 2016-05-30
 */
public interface ForestXmlConverter extends ForestConverter {

    /**
     * 将Java对象转换为XML字符串
     * @param obj
     * @return
     */
    String convertToXml(Object obj);

}
