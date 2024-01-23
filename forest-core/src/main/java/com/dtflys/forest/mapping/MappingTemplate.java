package com.dtflys.forest.mapping;


import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.exceptions.ForestTemplateSyntaxError;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ForestCache;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.utils.URLUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.*;

/**
 * @author gongjun
 * @since 2016-05-04
 */
public class MappingTemplate {
    private final static ForestCache<String, MappingTemplate> templateCache = new ForestCache<>(512);
    protected final Class<? extends Annotation> annotationType;
    protected final String attributeName;

    protected String template;
    protected List<MappingExpr> exprList;
    int readIndex = -1;

    protected volatile boolean compiled = false;

    public static MappingTemplate text(final String text) {
        return annotation(null, null, text);
    }

    public static MappingTemplate annotation(
            final Class<? extends Annotation> annotationType,
            final String attributeName,
            final String text) {
        final String key = (annotationType != null ? annotationType.getName() : "") + "@" + (attributeName != null ? attributeName : "") + "@" + text;
        return templateCache.get(key, k ->
                new MappingTemplate(annotationType, attributeName, text).compile());
    }


    private boolean isEnd(int index) {
        return index >= template.length() - 1;
    }

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
            throw new ForestTemplateSyntaxError("Template Expression Parse Error:\n Not found '" + except + "', column " + readIndex + " at \"" + template + "\"");
        }
        char real = nextChar();
        if (except != real) {
            throw new ForestTemplateSyntaxError("Template Expression Parse Error:\n It except '" + except + "', But found '" + real + "', column " + readIndex + " at \"" + template + "\"");
        }
    }

    private void matchToken(MappingExpr expr, Token token) {
        if (expr.token != token) {
            throw new ForestTemplateSyntaxError("Template Expression Parse Error:\n It except " + token.getName() + ", But found " + expr.token.getName() + ", column " + readIndex + " at \"" + template + "\"");
        }
    }

    private char watch(int i) {
        if (template.length() <= readIndex + i) {
            syntaxErrorWatchN(template.charAt(template.length() - 1), i);
        }
        return template.charAt(readIndex + i);
    }

    protected MappingTemplate(Class<? extends Annotation> annotationType, String attributeName, String template) {
        this.annotationType = annotationType;
        this.attributeName = attributeName;
        this.template = template;
        if (this.template == null) {
            this.template = "";
        }
    }

    public MappingTemplate compile() {
        readIndex = -1;
        exprList = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();

        while (!isEnd()) {
            char ch = nextChar();
            if (ch == '$') {
                char ch1 = watch(1);
                if (ch1 == '{') {
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
            else if (ch == '{') {
                if (buffer.length() > 0) {
                    MappingString str = new MappingString(buffer.toString());
                    exprList.add(str);
                }
                int oldIndex = readIndex;
                buffer = new StringBuffer();
                MappingExpr expr = null;
                try {
                    expr = parseExpression();
                } catch (ForestTemplateSyntaxError th) {
                    exprList.add(new MappingString("{"));
                    readIndex = oldIndex;
                    continue;
                }
                match('}');
                if (expr != null) {
                    expr = new MappingUrlEncodedExpr(expr);
                    exprList.add(expr);
                }
                continue;
            }
            if (ch == '#') {
                char ch1 = watch(1);
                if (ch1 == '{') {
                    nextChar();
                    if (buffer.length() > 0) {
                        MappingString str = new MappingString(buffer.toString());
                        exprList.add(str);
                    }
                    buffer = new StringBuffer();
                    MappingExpr expr = parseProperty();
                    match('}');
                    if (expr != null) {
                        exprList.add(expr);
                    }
                    continue;
                }
            }
            else if (ch == '\\') {
                char nc = watch(1);
                if (nc == '$' || nc == '{') {
                    ch = nextChar();
                    buffer.append(ch);
                } else {
                    buffer.append(ch);
                }
            }
            buffer.append(ch);
        }


        if (buffer.length() > 0) {
            MappingString str = new MappingString(buffer.toString());
            exprList.add(str);
        }
        compiled = true;
        return this;
    }

    public boolean hasIterateVariable() {
        for (MappingExpr expr : exprList) {
            if (expr.isIterateVariable()) {
                return true;
            }
        }
        return false;
    }


    private void syntaxErrorWatch1(char ch) {
        throw new ForestTemplateSyntaxError("Template Expression Parse Error:\n Character '" + ch +
                "', column " + (readIndex + 2) + " at \"" + template + "\"");
    }


    private void syntaxErrorWatchN(char ch, int n) {
        throw new ForestTemplateSyntaxError("Template Expression Parse Error:\n Character '" + ch +
                "', column " + (readIndex + n + 1) + " at \"" + template + "\"");
    }

    public MappingProperty parseProperty() {
        MappingProperty prop = null;
        char ch = watch(1);
        if (Character.isAlphabetic(ch) || ch == '_' || ch == '-') {
            prop = parsePropertyName();
        } else {
            syntaxErrorWatch1(ch);
        }
        return prop;
    }

    public MappingExpr parseExpression() {
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
                        syntaxErrorWatch1(ch);
                    }
                    if (expr instanceof MappingInteger) {
                        return new MappingIndex(((MappingInteger) expr).getNumber());
                    }
                    if (expr instanceof MappingIdentity) {
                        return new MappingReference(((MappingIdentity) expr).getName(), false);
                    }
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
                case '?':
                    nextChar();
                    if (expr instanceof MappingIdentity) {
                        expr = new MappingReference(((MappingIdentity) expr).getName(), true);
                    }
                    break;
                case '.':
                    nextChar();
                    if (expr instanceof MappingIdentity) {
                        expr = new MappingReference(((MappingIdentity) expr).getName(), false);
                    }
                    MappingIdentity id = parseIdentity();
                    expr = new MappingDot(expr, id);
                    break;
                case '(':
                    nextChar();
                    MappingIdentity methodName = null;
                    if (expr == null) {
                        syntaxErrorWatch1(ch);
                    }
                    if (expr.token == Token.DOT && ((MappingDot) expr).right != null) {
                        methodName = ((MappingDot) expr).right;
                        expr = ((MappingDot) expr).left;
                    }
                    else if (expr.token == Token.ID) {
                        methodName = (MappingIdentity) expr;
                        expr = null;
                    }
                    if (methodName == null || StringUtils.isEmpty(methodName.getName())) {
                        syntaxErrorWatch1(ch);
                    }
                    expr = parseInvokeParams(expr, methodName);
                    match(')');
                    break;
                default:
                    expr = parseLiteral();
                    break;
            }
        }
        return expr;
    }




    public MappingIdentity parseIdentity() {
        MappingExpr expr = parseTextToken();
        matchToken(expr, Token.ID);
        return (MappingIdentity) expr;
    }


    public MappingProperty parsePropertyName() {
        char ch = watch(1);
        StringBuilder builder = new StringBuilder();
        if (Character.isAlphabetic(ch) || ch == '_' || ch == '-') {
            do {
                builder.append(ch);
                nextChar();
                ch = watch(1);
            } while (Character.isAlphabetic(ch) ||
                    Character.isDigit(ch) ||
                    ch == '_' ||
                    ch == '-' ||
                    ch == '[' ||
                    ch == ']' ||
                    ch == '.');
        }
        String text = builder.toString();
        return new MappingProperty(text);
    }



    public MappingExpr parseTextToken() {
        char ch = watch(1);
        StringBuilder builder = new StringBuilder();
        if (Character.isAlphabetic(ch) || ch == '_') {
            do {
                builder.append(ch);
                nextChar();
                ch = watch(1);
            } while (Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_');
        }
        String text = builder.toString();
        if ("true".equals(text)) {
            return new MappingBoolean(true);
        }
        if ("false".equals(text)) {
            return new MappingBoolean(false);
        }
        return new MappingIdentity(text);
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


    public MappingInvoke parseInvokeParams(MappingExpr left, MappingIdentity name) {
        return parseMethodParams_inner(left, name, new ArrayList<>());
    }


    public MappingInvoke parseMethodParams_inner(MappingExpr left, MappingIdentity name, List<MappingExpr> argExprList) {
        skipSpaces();
        char ch = watch(1);
        switch (ch) {
            case ')':
                if (left == null) {
                    return new MappingFilterInvoke(name, argExprList);
                }
                return new MappingInvoke(left, name, argExprList);
            case ',':
                nextChar();
                MappingExpr expr = parseExpression();
                argExprList.add(expr);
                return parseMethodParams_inner(left, name, argExprList);
            default:
                MappingExpr expr2 = parseExpression();
                argExprList.add(expr2);
                return parseMethodParams_inner(left, name, argExprList);
        }
    }



    public MappingExpr parseLiteral() {
        char ch = watch(1);
        if (Character.isDigit(ch)) {
            StringBuilder builder = new StringBuilder();
            do {
                builder.append(ch);
                nextChar();
                ch = watch(1);
            } while (Character.isDigit(ch));
            if (ch == '.') {
                char ch2 = watch(2);
                if (Character.isDigit(ch2)) {
                    builder.append('.');
                    nextChar();
                    do {
                        builder.append(ch);
                        nextChar();
                        ch = watch(1);
                    } while (Character.isDigit(ch));
                    if (ch == 'f' || ch == 'F') {
                        nextChar();
                        return new MappingFloat(Float.parseFloat(builder.toString()));
                    }
                    if (ch == 'd' || ch == 'D') {
                        nextChar();
                        return new MappingDouble(Double.parseDouble(builder.toString()));
                    }
                    return new MappingFloat(Float.parseFloat(builder.toString()));
                }
            }
            else if (ch == 'l' || ch == 'L') {
                return new MappingLong(Long.parseLong(builder.toString()));
            }
            else {
                return new MappingInteger(Integer.parseInt(builder.toString()));
            }
        }
        else if (Character.isAlphabetic(ch) || ch == '_') {
            return parseTextToken();
        }
        syntaxErrorWatch1(ch);
        return null;
    }

    protected String renderExpression(VariableValueContext valueContext, MappingExpr expr) {
        Object val = null;
        MappingParameter param = null;
        final ForestJsonConverter jsonConverter = valueContext.getConfiguration().getJsonConverter();
        final ForestMethod method = valueContext.getForestMethod();
        final MappingParameter[] parameters = method != null ? method.getParameters() : null;
        if (expr instanceof MappingString) {
            return ((MappingString) expr).getText();
        } else if (expr instanceof MappingIndex && parameters != null) {
            try {
                Integer index = ((MappingIndex) expr).getIndex();
                param = parameters[index];
                val = valueContext.getArgument(index);
            } catch (Exception ex) {
            }
            if (val != null) {
                val = getParameterValue(jsonConverter, val);
                if (param != null && param.isUrlEncode()) {
                    val = URLUtils.queryValueEncode(String.valueOf(val), param.getCharset());
                }
                return String.valueOf(val);
            }
        } else {
            val = expr.render(valueContext);
            if (val != null) {
                val = getParameterValue(jsonConverter, val);
                if (param != null && param.isUrlEncode()) {
                    val = URLUtils.pathEncode(String.valueOf(val), param.getCharset());
                }
            }
            return String.valueOf(val);
        }
        return null;
    }


    public String render(VariableValueContext valueContext) {
        try {
            int len = exprList.size();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < len; i++) {
                MappingExpr expr = exprList.get(i);
                String val = renderExpression(valueContext, expr);
                if (val != null) {
                    builder.append(val);
                }
            }
            return builder.toString();
        } catch (ForestVariableUndefinedException ex) {
            throw new ForestVariableUndefinedException(annotationType, attributeName, valueContext, ex.getVariableName(), template);
        }
    }



    public static String getParameterValue(ForestJsonConverter jsonConverter, Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map || obj instanceof Collection) {
            return jsonConverter.encodeToString(obj);
        }
        if (obj instanceof Date) {
            return String.valueOf(((Date) obj).getTime());
        }
        return obj.toString();
    }


    private static String getFormCollectionValueString(Collection collection) {
        if (collection.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
            Object item = iterator.next();
            builder.append(item);
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        return builder.toString();
    }


    private static String getFormArrayValueString(Object array) {
        StringBuilder builder = new StringBuilder();
        int len = Array.getLength(array);
        if (len == 0) {
            return null;
        }
        for (int i = 0; i < len; i++) {
            Object item = Array.get(array, i);
            builder.append(item);
            if (i < len - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }



    public static String getFormValueString(ForestJsonConverter jsonConverter, Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            return getFormCollectionValueString(collection);
        }
        if (obj.getClass().isArray()) {
            return getFormArrayValueString(obj);
        }
        if (obj instanceof Map) {
            return jsonConverter.encodeToString(obj);
        }
        if (obj instanceof Date) {
            return String.valueOf(((Date) obj).getTime());
        }
        return obj.toString();
    }

    @Override
    public MappingTemplate clone() {
        MappingTemplate template = new MappingTemplate(annotationType, attributeName, this.template);
        template.exprList = this.exprList;
        return template;
    }


    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        for (MappingExpr expr : exprList) {
            buff.append(expr.toString());
        }
        return buff.toString();
    }

    public MappingTemplate valueOf(String value) {
        return new MappingTemplate(
                annotationType, attributeName, value);
    }


}
