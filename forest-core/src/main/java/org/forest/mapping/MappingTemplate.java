package org.forest.mapping;


import org.forest.config.VariableScope;
import org.forest.converter.json.ForestJsonConverter;
import org.forest.reflection.ForestMethod;

import java.util.*;

/**
 * @author gongjun
 * @since 2016-05-04
 */
public class MappingTemplate {
    private  String template;
    private List<MappingExpr> exprList;
    private VariableScope variableScope;

    int readIndex = -1;

    private boolean isEnd() {
        return readIndex >= template.length() - 1;
    }

    private char nextChar() {
        readIndex++;
        return template.charAt(readIndex);
    }

    private char watchChar(int i) {
        return template.charAt(readIndex + i);
    }

    public MappingTemplate(String template, VariableScope variableScope) {
        this.template = template;
        this.variableScope = variableScope;
        compile();
    }

    public void compile() {
        readIndex = -1;
        exprList = new ArrayList<MappingExpr>();
        StringBuffer buffer = new StringBuffer();

        while (!isEnd()) {
            char ch = nextChar();
            if (ch == '$') {
                if (watchChar(1) == '{') {
                    nextChar();
                    if (buffer.length() > 0) {
                        MappingString str = new MappingString(buffer.toString());
                        exprList.add(str);
                    }

                    buffer = new StringBuffer();
                    MappingExpr expr = parseVariable();
                    if (expr != null) {
                        exprList.add(expr);
                    }
                    continue;
                }
            }
            buffer.append(ch);
        }


        if (buffer.length() > 0) {
            MappingString str = new MappingString(buffer.toString());
            exprList.add(str);
        }
    }

    public MappingExpr parseVariable() {
        StringBuffer buffer = new StringBuffer();
        MappingExpr expr = null;
        while (!isEnd()) {
            char ch = nextChar();
            switch (ch) {
                case '}':
                    if (expr == null) {
                        expr = parseExpr(buffer);
                    }
                    else {
                        expr = new MappingDot(variableScope, expr, buffer.toString());
                    }
                    return expr;
                case '.':
                    if (expr == null) {
                        expr = parseExpr(buffer);
                    }
                    else {
                        expr = new MappingDot(variableScope, expr, buffer.toString());
                    }
                    buffer = new StringBuffer();
                    break;
                default:
                    buffer.append(ch);
                    break;
            }

        }
        return expr;
    }

    public MappingExpr parseExpr(StringBuffer buffer) {
        String str = buffer.toString();
        Integer num = null;
        try {
            num = Integer.parseInt(str);
        } catch (Exception ex) {
        }
        if (num != null) {
            return new MappingIndex(Integer.parseInt(str));
        }
        else {
            return new MappingReference(variableScope, buffer.toString());
        }
    }

    public String render(Object[] args) {
        ForestJsonConverter jsonConverter = variableScope.getConfiguration().getJsonCoverter();
        int len = exprList.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            MappingExpr expr = exprList.get(i);
            Object val = null;
            if (expr instanceof MappingString) {
                builder.append(((MappingString) expr).getText());
            }
            else if (expr instanceof MappingIndex) {
                try {
                    val = args[((MappingIndex) expr).getIndex()];
                } catch (Exception ex) {
                }
                if (val != null) {
                    builder.append(getParameterValue(jsonConverter, val));
                }
            }
            else {
                val = expr.render(args);
                if (val != null) {
                    builder.append(getParameterValue(jsonConverter, val));
                }
            }
        }
        return builder.toString();
    }

    public static String getParameterValue(ForestJsonConverter jsonConverter, Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof Map || obj instanceof Collection) {
            return jsonConverter.convertToJson(obj);
        }
        if (obj instanceof Date) {
            return String.valueOf(((Date) obj).getTime());
        }
        return obj.toString();
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        for (MappingExpr expr : exprList) {
            buff.append(expr.toString());
        }
        return buff.toString();
    }

    public MappingTemplate valueOf(String value, ForestMethod forestMethod) {
        return new MappingTemplate(value, forestMethod);
    }
}
