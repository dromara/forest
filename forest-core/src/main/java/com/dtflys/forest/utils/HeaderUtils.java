package com.dtflys.forest.utils;

import com.dtflys.forest.http.ForestHeaderMap;
import com.dtflys.forest.http.HasHeaders;
import com.dtflys.forest.mapping.MappingTemplate;

public class HeaderUtils {

    public static String[] splitHeaderNameValue(final String headerText) {
        return headerText.split(":", 2);
    }

    public static HasHeaders addHeaders(
            final HasHeaders hasHeaders, final MappingTemplate[] headerTemplates, final Object[] args) {
        final ForestHeaderMap headerMap = hasHeaders.getHeaders();
        if (headerTemplates != null && headerTemplates.length > 0) {
            for (MappingTemplate baseHeader : headerTemplates) {
                String headerText = baseHeader.render(args);
                String[] headerNameValue = headerText.split(":", 2);
                if (headerNameValue.length > 1) {
                    String name = headerNameValue[0].trim();
                    String value = headerNameValue[1].trim();
                    if (headerMap.getHeader(name) == null) {
                        headerMap.addHeader(name, value);
                    }
                }
            }
        }
        return hasHeaders;
    }
}
