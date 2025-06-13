package com.dtflys.forest.utils;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingTemplate;

public class TemplateUtils {


    public static String readString(String text, ForestRequest request) {
        return readString(text, request, true);
    }

    public static String readString(String text, ForestRequest request, boolean allowEmptyBrace) {
        return readString(text, request, request.arguments(), allowEmptyBrace);
    }


    public static String readString(String text, VariableScope scope, Object[] args) {
        return readString(text, scope, args, true);
    }

    public static String readString(String text, VariableScope scope, Object[] args, boolean allowEmptyBrace) {
        if (text == null) {
            return null;
        }
        if (text.isEmpty()) {
            return text;
        }
        final MappingTemplate template = MappingTemplate.create(scope, text, allowEmptyBrace);
        return template.render(scope, args, allowEmptyBrace);
    }

}
