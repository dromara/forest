package com.dtflys.forest.mapping;


import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.ForestProperties;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.exceptions.*;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;
import com.dtflys.forest.reflection.ForestMethod;
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
    protected Class<? extends Annotation> annotationType;
    protected String attributeName;
    protected ForestMethod<?> forestMethod;
    protected MappingParameter[] parameters;
    protected String template;
    protected List<MappingExpr> exprList;
    protected MappingCompileContext context = new MappingCompileContext();
    protected final int startReadIndex;
    protected final int endReadIndex;

    private boolean isEnd() {
        return context.readIndex >= endReadIndex - 1;
    }

    private char nextChar() {
        context.readIndex++;
        return template.charAt(context.readIndex);
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
            throw new ForestTemplateSyntaxError("Template Expression Parse Error:\n Not found '" + except + "', column " + context.readIndex + " at \"" + template + "\"");
        }
        char real = nextChar();
        if (except != real) {
            throw new ForestTemplateSyntaxError("Template Expression Parse Error:\n It except '" + except + "', But found '" + real + "', column " + context.readIndex + " at \"" + template + "\"");
        }
    }

    private void matchToken(MappingExpr expr, Token token) {
        if (expr.token != token) {
            throw new ForestTemplateSyntaxError("Template Expression Parse Error:\n It except " + token.getName() + ", But found " + expr.token.getName() + ", column " + context.readIndex + " at \"" + template + "\"");
        }
    }

    private char watch(int i) {
        if (template.length() <= context.readIndex + i) {
            syntaxErrorWatchN(template.charAt(template.length() - 1), i);
        }
        return template.charAt(context.readIndex + i);
    }

    public static MappingTemplate create(final String template) {
        return create(null, template);
    }

    public static MappingTemplate create(final VariableScope scope, final String template) {
        return create(scope, template, 0, template == null ? 0 : template.length());
    }
    
    public static MappingTemplate create(final VariableScope scope, final String template, final int startReadIndex, final int endReadIndex) {
        if (scope instanceof RequestVariableScope) {
            final ForestRequest request = ((RequestVariableScope) scope).asRequest();
            return new MappingTemplate(
                    null,
                    null,
                    request.getMethod(),
                    template,
                    request.getMethod().getParameters(),
                    startReadIndex,
                    endReadIndex
            );
        } else {
            return new MappingTemplate(
                    null,
                    null,
                    null,
                    template,
                    null,
                    startReadIndex,
                    endReadIndex
            );
        }
    }

    public MappingTemplate(Class<? extends Annotation> annotationType, String attributeName, ForestMethod<?> forestMethod, String template, MappingParameter[] parameters) {
        this(annotationType, attributeName, forestMethod, template, parameters, 0, template == null ? 0 : template.length());
    }


    public MappingTemplate(Class<? extends Annotation> annotationType, String attributeName, ForestMethod<?> forestMethod, String template, MappingParameter[] parameters, int startReadIndex, int endReadIndex) {
        this.annotationType = annotationType;
        this.attributeName = attributeName;
        this.forestMethod = forestMethod;
        this.template = template;
        this.parameters = parameters;
        this.startReadIndex = startReadIndex;
        this.endReadIndex = endReadIndex;
        if (this.template == null) {
            this.template = "";
        }
        compile();
    }

    public void bindScope(VariableScope scope) {
        if (scope instanceof RequestVariableScope) {
            ForestRequest request = ((RequestVariableScope) scope).asRequest();
            if (request != null) {
                this.forestMethod = request.getMethod();
                this.parameters = request.getMethod().getParameters();
            }
        }
    }

    public void bindAnnotationAttribute(Class<? extends Annotation> annotationType, String attributeName) {
        this.annotationType = annotationType;
        this.attributeName = attributeName;
    }
    
    public void compile() {
        context.readIndex = -1 + startReadIndex;
        exprList = new ArrayList<>(8);
        StringBuilder buffer = new StringBuilder();
        int startIndex = context.readIndex;

        while (!isEnd()) {
            char ch = nextChar();
            switch (ch) {
                case '$': {
                    char ch1 = watch(1);
                    if (ch1 == '{') {
                        nextChar();
                        if (buffer.length() > 0) {
                            MappingString str = new MappingString(buffer.toString(), startIndex, startIndex + buffer.length());
                            startIndex = context.readIndex;
                            exprList.add(str);
                        }

                        buffer = new StringBuilder();
                        MappingExpr expr = parseExpression();
                        match('}');
                        if (expr != null) {
                            exprList.add(expr);
                        }
                        continue;
                    }
                    break;
                }
                case '{': {
                    if (buffer.length() > 0) {
                        MappingString str = new MappingString(buffer.toString(), startIndex, startIndex + buffer.length());
                        startIndex = context.readIndex;
                        exprList.add(str);
                    }
                    int oldIndex = context.readIndex;
                    buffer = new StringBuilder();
                    MappingExpr expr = null;
                    try {
                        expr = parseExpression();
                    } catch (ForestTemplateSyntaxError th) {
                        exprList.add(new MappingString("{", startIndex, startIndex + 1));
                        context.readIndex = oldIndex;
                        startIndex = context.readIndex;
                        continue;
                    }
                    match('}');
                    if (expr != null) {
                        expr = new MappingUrlEncodedExpr(this, expr);
                        exprList.add(expr);
                    }
                    continue;
                }
                case '#': {
                    char ch1 = watch(1);
                    if (ch1 == '{') {
                        nextChar();
                        if (buffer.length() > 0) {
                            MappingString str = new MappingString(buffer.toString(), startIndex, startIndex + buffer.length());
                            startIndex = context.readIndex;
                            exprList.add(str);
                        }
                        buffer = new StringBuilder();
                        MappingExpr expr = parseProperty();
                        match('}');
                        if (expr != null) {
                            exprList.add(expr);
                        }
                        continue;
                    }
                    break;
                }
                case '\\': {
                    char nc = watch(1);
                    if (nc == '$' || nc == '{') {
                        ch = nextChar();
                        buffer.append(ch);
                    } else {
                        buffer.append(ch);
                    }
                }
            }
            buffer.append(ch);
        }

        if (buffer.length() > 0) {
            MappingString str = new MappingString(buffer.toString(), startIndex, startIndex + buffer.length());
            exprList.add(str);
        }
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
                "', column " + (context.readIndex + 2) + " at \"" + template + "\"");
    }


    private void syntaxErrorWatchN(char ch, int n) {
        throw new ForestTemplateSyntaxError("Template Expression Parse Error:\n Character '" + ch +
                "', column " + (context.readIndex + n + 1) + " at \"" + template + "\"");
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
        int startIndex = context.readIndex + 1;
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
                        if (startIndex > 1 && watch(0) != '{') {
                            return null;
                        }
                        return new MappingIndex(this, ++context.argumentIndex, startIndex, context.readIndex + 1);
                    }
                    if (expr instanceof MappingInteger) {
                        final int idx = ((MappingInteger) expr).getNumber();
                        context.argumentIndex = idx;
                        return new MappingIndex(this, context.argumentIndex, startIndex, context.readIndex + 1);
                    }
                    if (expr instanceof MappingIdentity) {
                        return new MappingReference(this, ((MappingIdentity) expr).getName(), expr.startIndex, expr.endIndex);
                    }
                    return expr;
                case '\'':
                    nextChar();
                    expr = parseString(ch, false);
                    match('\'');
                    break;
                case '\"':
                    nextChar();
                    expr = parseString(ch, false);
                    match('\"');
                    break;
                case '`':
                    nextChar();
                    expr = parseString(ch, true);
                    match('`');
                    break;
                case '$':
                    nextChar();
                    expr = parseIndex();
                    break;
                case '?':
                    nextChar();
                    ch = watch(1);
                    if (ch == '.') {
                        nextChar();
                        expr = parseOptionalDot(expr, startIndex);
                    } else if (ch == '?') {
                        nextChar();
                        expr = parseElvisExpr(expr, startIndex);
                    } else {
                        expr = parseOptionExpr(expr, startIndex);
                    }
                    break;
                case '.':
                    nextChar();
                    expr = parseDot(expr);
                    break;
                case '(':
                    nextChar();
                    MappingIdentity methodName = null;
                    if (expr == null) {
                        syntaxErrorWatch1(ch);
                    }
                    if ((expr.token == Token.DOT || expr.token == Token.OPTIONAL_DOT) &&
                            ((MappingDot) expr).right != null) {
                        methodName = ((MappingDot) expr).right;
                        expr = ((MappingDot) expr).left;
                    } else if (expr.token == Token.ID) {
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

    public MappingDot parseDot(final MappingExpr leftExpr) {
        final MappingExpr newExpr = leftExpr instanceof MappingIdentity ?
                new MappingReference(this, ((MappingIdentity) leftExpr).getName(), leftExpr.startIndex, leftExpr.endIndex) :
                leftExpr;
        if (newExpr == null) {
            throw new ForestTemplateSyntaxError("There is no valid token before '.'");
        }
        final MappingIdentity id = parseIdentity();
        return new MappingDot(this, newExpr, id, newExpr.startIndex, id.endIndex);
    }

    public MappingOptionalDot parseOptionalDot(final MappingExpr leftExpr, final int startIndex) {
        final MappingExpr newExpr = leftExpr instanceof MappingIdentity ?
                new MappingReference(
                        this,
                        ((MappingIdentity) leftExpr).getName(),
                        true,
                        leftExpr.startIndex, leftExpr.endIndex) :
                leftExpr;
        if (newExpr == null) {
            throw new ForestTemplateSyntaxError("There is no valid token before '?.'");
        }
        final MappingIdentity id = parseIdentity();
        return new MappingOptionalDot(this, newExpr, id, startIndex, id.endIndex);
    }

    public MappingElvisExpr parseElvisExpr(final MappingExpr leftExpr, final int startIndex) {
        final MappingExpr newExpr = leftExpr instanceof MappingIdentity ?
                new MappingReference(
                        this,
                        ((MappingIdentity) leftExpr).getName(),
                        true,
                        leftExpr.startIndex, leftExpr.endIndex) :
                leftExpr;
        if (newExpr == null) {
            throw new ForestExpressionException("Unexpected token '??'", annotationType, attributeName, forestMethod, template, context.readIndex - 1, context.readIndex + 1);
        }
        final MappingExpr rightExpr = parseExpression();
        if (rightExpr == null) {
            throw new ForestExpressionException("Unexpected token '??'", annotationType, attributeName, forestMethod, template, context.readIndex - 1, context.readIndex + 1);
        }
        return new MappingElvisExpr(this, newExpr, rightExpr, startIndex, rightExpr.endIndex);
    }

    public MappingExpr parseOptionExpr(final MappingExpr leftExpr, final int startIndex) {
        final MappingExpr newExpr = leftExpr instanceof MappingIdentity ?
                new MappingReference(
                        this,
                        ((MappingIdentity) leftExpr).getName(),
                        true,
                        leftExpr.startIndex, leftExpr.endIndex) :
                leftExpr;
        if (newExpr == null) {
            throw new ForestTemplateSyntaxError("There is no valid token before '?'");
        }
        return newExpr;
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
        return new MappingProperty(this, text);
    }



    public MappingExpr parseTextToken() {
        char ch = watch(1);
        int startIndex = context.readIndex + 1;
        StringBuilder builder = new StringBuilder();
        if (Character.isAlphabetic(ch) || ch == '_') {
            do {
                builder.append(ch);
                nextChar();
                ch = watch(1);
            } while (Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_');
        }
        int endIndex = context.readIndex + 1;
        String text = builder.toString();
        if ("true".equals(text)) {
            return new MappingBoolean(this, true, startIndex, endIndex);
        }
        if ("false".equals(text)) {
            return new MappingBoolean(this, false, startIndex, endIndex);
        }
        return new MappingIdentity(this, text, startIndex, endIndex);
    }


    public MappingIndex parseIndex() {
        char ch = watch(1);
        int index = 0;
        int n = 0;
        int startIndex = context.readIndex + 1;
        while (Character.isDigit(ch)) {
            int x = ch - '0';
            index = 10 * n + x;
            n++;
            nextChar();
            ch = watch(1);
        }
        int endIndex = context.readIndex + 1;
        return new MappingIndex(this, index, startIndex, endIndex);
    }


    public MappingExpr parseString(char quoteChar, boolean isTemplateString) {
        StringBuilder builder = new StringBuilder();
        char ch = watch(1);
        int startIndex = context.readIndex + 1;
        while (ch != quoteChar && !isEnd()) {
            builder.append(ch);
            nextChar();
            ch = watch(1);
        }
        int endIndex = context.readIndex + 1;
        if (isTemplateString) {
            return new MappingTemplateString(this, template, startIndex, endIndex);
        }
        return new MappingString(builder.toString(), startIndex, endIndex);
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
                    return new MappingFilterInvoke(this, name, argExprList, !argExprList.isEmpty() ? argExprList.get(0).startIndex : context.readIndex, context.readIndex);
                }
                return new MappingInvoke(this, left, name, argExprList, left.startIndex, context.readIndex);
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
                        return new MappingFloat(this, Float.parseFloat(builder.toString()));
                    }
                    if (ch == 'd' || ch == 'D') {
                        nextChar();
                        return new MappingDouble(this, Double.parseDouble(builder.toString()));
                    }
                    return new MappingFloat(this, Float.parseFloat(builder.toString()));
                }
            }
            else if (ch == 'l' || ch == 'L') {
                return new MappingLong(this, Long.parseLong(builder.toString()));
            }
            else {
                return new MappingInteger(this, Integer.parseInt(builder.toString()));
            }
        }
        else if (Character.isAlphabetic(ch) || ch == '_') {
            return parseTextToken();
        }
        syntaxErrorWatch1(ch);
        return null;
    }

    protected String renderExpression(VariableScope scope, ForestJsonConverter jsonConverter, MappingExpr expr, Object[] args) {
        Object val = null;
        MappingParameter param = null;
        if (expr instanceof MappingString) {
            return ((MappingString) expr).getText();
        } else if (expr instanceof MappingIndex) {
            Integer index = ((MappingIndex) expr).getIndex();
            if (parameters != null && index < parameters.length) {
                param = parameters[index];
            }
            if (args != null && index < args.length) {
                val = args[index];
            }
            if (val != null) {
                val = getParameterValue(jsonConverter, val);
                if (param != null && param.isUrlEncode()) {
                    val = URLUtils.queryValueEncode(String.valueOf(val), param.getCharset());
                }
                return String.valueOf(val);
            }
        } else {
            val = expr.render(scope, args);
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


    public String render(VariableScope scope, Object[] args) {
        final ForestJsonConverter jsonConverter = scope.getConfiguration().getJsonConverter();
        int len = exprList.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            MappingExpr expr = exprList.get(i);
            String val = renderExpression(scope, jsonConverter, expr, args);
            if (val != null) {
                builder.append(val);
            }
        }
        return builder.toString();
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
        MappingTemplate template = new MappingTemplate(annotationType, attributeName, forestMethod, this.template, this.parameters);
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
                annotationType, attributeName, forestMethod, value, forestMethod.getParameters());
    }


}
