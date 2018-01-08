package org.forest.mapping;


import org.forest.config.VariableScope;
import org.forest.converter.json.ForestJsonConverter;
import org.forest.exceptions.ForestRuntimeException;
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

    private void skipSpaces() {
        if (isEnd()) {
            return;
        }
        char ch = watch(1);
        while ((ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') && !isEnd()) {
            nextChar();
            ch = watch(1);
        }
    }

    private void match(char except) {
        if (isEnd()) {
            throw new ForestRuntimeException("Template Expression Parse Error:\n Not found '" + except + "', column " + readIndex + " at \"" + template + "\"");
        }
        char real = nextChar();
        if (except != real) {
            throw new ForestRuntimeException("Template Expression Parse Error:\n It need '" + except + "', But found '" + real + "', column " + readIndex + " at \"" + template + "\"");
        }
    }

    private char watch(int i) {
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
                if (watch(1) == '{') {
                    nextChar();
                    if (buffer.length() > 0) {
                        MappingString str = new MappingString(buffer.toString());
                        exprList.add(str);
                    }

                    buffer = new StringBuffer();
                    MappingExpr expr = parseExpression();
                    match('}');
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

    public MappingExpr parseExpression() {
        StringBuffer buffer = new StringBuffer();
        MappingExpr expr = null;
        while (!isEnd()) {
            char ch = watch(1);
            switch (ch) {
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    skipSpaces();
                    break;
                case ')':
                case '}':
                    if (expr == null) {
                        expr = parseExpr(buffer);
                    }
//                    else {
//                        expr = new MappingDot(variableScope, expr, buffer.toString());
//                    }
                    return expr;
                case '\'':
                    nextChar();
                    expr = parseString(ch);
                    match('\'');
                    break;
                case '\"':
                    nextChar();
                    expr = parseString(ch);
                    match('\"');
                    break;
                case '$':
                    nextChar();
                    expr = parseIndex();
                    break;
                case '.':
                    nextChar();
                    if (expr == null) {
                        expr = parseExpr(buffer);
                    }
                    String right = parseIdentity();
                    expr = new MappingDot(variableScope, expr, right);
                    buffer = new StringBuffer();
                    break;
                case '(':
                    nextChar();
                    String methodName = buffer.toString();
//                    if (StringUtils.isEmpty(methodName)) {
//                        throw new ForestRuntimeException("Template Expression Parse Error:\n Character '" + ch + "', column " + readIndex + " at \"" + template + "\"");
//                    }
                    if (expr instanceof MappingDot && ((MappingDot) expr).right != null) {
                        methodName = ((MappingDot) expr).right;
                        expr = ((MappingDot) expr).left;
                    }
                    expr = parseInvokeParams(variableScope, expr, methodName);
                    match(')');
                    break;
                default:
                    nextChar();
                    buffer.append(ch);
                    break;
            }

        }
        return expr;
    }


    public String parseIdentity() {
        char ch = watch(1);
        StringBuilder builder = new StringBuilder();
        while (Character.isAlphabetic(ch)) {
            builder.append(ch);
            nextChar();
            ch = watch(1);
        }
        return builder.toString();
    }


    public MappingIndex parseIndex() {
        char ch = watch(1);
        int index = 0;
        int n = 0;
        while (Character.isDigit(ch)) {
            int x = ch - '0';
            index = 10 * n + x;
            n++;
            nextChar();
            ch = watch(1);
        }
        return new MappingIndex(index);
    }


    public MappingString parseString(char quoteChar) {
        StringBuilder builder = new StringBuilder();
        char ch = watch(1);
        while (ch != quoteChar && !isEnd()) {
            builder.append(ch);
            nextChar();
            ch = watch(1);
        }
        return new MappingString(builder.toString());
    }


    public MappingInvoke parseInvokeParams(VariableScope variableScope, MappingExpr left, String name) {
        return parseMethodParams_inner(variableScope, left, name, new ArrayList<MappingExpr>());
    }


    public MappingInvoke parseMethodParams_inner(VariableScope variableScope, MappingExpr left, String name, List<MappingExpr> argExprList) {
        skipSpaces();
        char ch = watch(1);
        switch (ch) {
            case ')':
                return new MappingInvoke(variableScope, left, name, argExprList);
            case ',':
                nextChar();
                MappingExpr expr = parseExpression();
                argExprList.add(expr);
                return parseMethodParams_inner(variableScope, left, name, argExprList);
        }
        throw new ForestRuntimeException("Template Expression Parse Error:\n Character '" + ch + "', column " + readIndex + " at \"" + template + "\"");
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
        ForestJsonConverter jsonConverter = variableScope.getConfiguration().getJsonConverter();
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
