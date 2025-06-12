package com.dtflys.forest.utils;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingTemplate;

public class TemplateUtils {


    public static String readString(String text, ForestRequest request) {
        return readString(text, request, request.arguments());
    }

    public static String readString(String text, VariableScope scope, Object[] args) {
        if (text == null) {
            return null;
        }
        if (text.isEmpty()) {
            return text;
        }
        final MappingTemplate template = MappingTemplate.create(scope, text);
        return template.render(scope, args);
    }

}
