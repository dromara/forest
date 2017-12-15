package org.forest.filter;

import org.forest.config.ForestConfiguration;
import org.forest.converter.xml.ForestXmlConverter;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-12-15 14:56
 */
public class XmlFilter implements Filter {

    @Override
    public Object doFilter(ForestConfiguration configuration, Object data) {
        ForestXmlConverter xmlConverter = configuration.getXmlConverter();
        String xml = xmlConverter.convertToXml(data);
        return xml;
    }

}
