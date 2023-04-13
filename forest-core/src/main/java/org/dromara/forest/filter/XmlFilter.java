package org.dromara.forest.filter;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.converter.xml.ForestXmlConverter;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-12-15 14:56
 */
public class XmlFilter implements Filter {

    @Override
    public Object doFilter(ForestConfiguration configuration, Object data) {
        ForestXmlConverter xmlConverter = configuration.getXmlConverter();
        String xml = xmlConverter.encodeToString(data);
        return xml;
    }

}
