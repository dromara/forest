package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestConfiguration;
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
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author gongjun
 * @since 2016-05-04
 */
public class MappingTemplate {

    public final static int TEMPLATE = 0;
    public final static int METHOD_TEMPLATE = 1;
    public final static int URL = 2;
    public final static int METHOD_URL = 3;
    public final static int GLOBAL_VARIABLE = 4;
    public final static int METHOD_VARIABLE = 5;
    public final static int REQUEST_VARIABLE = 6;

    protected final int type;
    protected Class<? extends Annotation> annotationType;
    protected String attributeName;
    protected ForestMethod<?> forestMethod;
    protected MappingParameter[] parameters;
    protected String source;
    protected List<MappingExpr> exprList;
    protected MappingCompileContext context = new MappingCompileContext();
    protected final int startReadIndex;
    protected final int endReadIndex;

    public static class CompileContext {
        boolean allowEmptyBraces = true;

        public CompileContext(boolean allowEmptyBraces) {
            this.allowEmptyBraces = allowEmptyBraces;
        }
    }

    private boolean isEnd() {
        return context.readIndex >= endReadIndex - 1;
    }

    private char nextChar() {
        context.readIndex++;
        return source.charAt(context.readIndex);
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

    private void throwSyntaxError(String message) {
        throwSyntaxError(message, context.readIndex, Math.min(context.readIndex + 1, message.length()));
    }


    private void throwSyntaxError(String message, int startIndex, int endIndex) {
        throw new ForestTemplateSyntaxError(message, annotationType, attributeName, forestMethod, this, startIndex, endIndex);
    }


    private void match(char except) {
        if (isEnd()) {
            throwSyntaxError("Template Expression Parse Error:\n Not found '" + except + "', column " + context.readIndex + " at \"" + source + "\"");
        }
        char real = nextChar();
        if (except != real) {
            throwSyntaxError("Template Expression Parse Error:\n It except '" + except + "', But found '" + real + "', column " + context.readIndex + " at \"" + source + "\"");
        }
    }

    private void matchToken(MappingExpr expr, Token token) {
        if (expr.token != token) {
            throwSyntaxError("Template Expression Parse Error:\n It except " + token.getName() + ", But found " + expr.token.getName() + ", column " + context.readIndex + " at \"" + source + "\"");
        }
    }

    private char watch(int i) {
        if (source.length() <= context.readIndex + i) {
            syntaxErrorWatchN(source.charAt(source.length() - 1), i);
        }
        return source.charAt(context.readIndex + i);
    }

    private static int typeFromScope(final VariableScope scope) {
        if (scope instanceof RequestVariableScope) {
            return REQUEST_VARIABLE;
        }
        if (scope instanceof ForestConfiguration) {
            return GLOBAL_VARIABLE;
        }
        if (scope instanceof ForestMethod) {
            return METHOD_VARIABLE;
        }
        return TEMPLATE;
    }

    public static MappingTemplate createVariableTemplate(final String variableName, final VariableScope scope, final String source) {
        final MappingTemplate template = create(typeFromScope(scope), scope, source);
        template.attributeName = variableName;
        return template;
    }

    public static MappingTemplate create(final String source) {
        return create(TEMPLATE, source, true);
    }


    public static MappingTemplate create(final String source, final boolean allowEmptyBrace) {
        return create(TEMPLATE, source, allowEmptyBrace);
    }

    public static MappingTemplate create(final int type, final String source) {
        return create(type, null, source, true);
    }

    public static MappingTemplate create(final int type, final String source, final boolean allowEmptyBrace) {
        return create(type, null, source, allowEmptyBrace);
    }

    public static MappingTemplate create(final VariableScope scope, final String source) {
        return create(TEMPLATE, scope, source, true);
    }

    public static MappingTemplate create(final VariableScope scope, final String source, final boolean allowEmptyBrace) {
        return create(TEMPLATE, scope, source, allowEmptyBrace);
    }

    public static MappingTemplate create(final int type, final VariableScope scope, final String source) {
        return create(type, scope, source, true, 0, source == null ? 0 : source.length());
    }


    public static MappingTemplate create(final int type, final VariableScope scope, final String source, final boolean allowEmptyBrace) {
        return create(type, scope, source, allowEmptyBrace, 0, source == null ? 0 : source.length());
    }

    public static MappingTemplate create(final VariableScope scope, final String source, final int startReadIndex, final int endReadIndex) {
        return create(TEMPLATE, scope, source, true, startReadIndex, endReadIndex);
    }


    public static MappingTemplate create(final VariableScope scope, final String source, final boolean allowEmptyBrace, final int startReadIndex, final int endReadIndex) {
        return create(TEMPLATE, scope, source, allowEmptyBrace, startReadIndex, endReadIndex);
    }
    
    public static MappingTemplate create(final int type, final VariableScope scope, final String source, final boolean allowEmptyBrace, final int startReadIndex, final int endReadIndex) {
        if (scope instanceof RequestVariableScope) {
            final ForestRequest request = ((RequestVariableScope) scope).asRequest();
            final ForestMethod method = request.getMethod();
            return new MappingTemplate(
                    type,
                    null,
                    null,
                    method,
                    source,
                    method != null ? method.getParameters() : null,
                    allowEmptyBrace,
                    startReadIndex,
                    endReadIndex
            );
        } else {
            return new MappingTemplate(
                    type,
                    null,
                    null,
                    null,
                    source,
                    null,
                    allowEmptyBrace,
                    startReadIndex,
                    endReadIndex
            );
        }
    }

    public MappingTemplate(int type, Class<? extends Annotation> annotationType, String attributeName, ForestMethod<?> forestMethod, String source, MappingParameter[] parameters) {
        this(type, annotationType, attributeName, forestMethod, source, parameters, true, 0, source == null ? 0 : source.length());
    }

    public MappingTemplate(int type, Class<? extends Annotation> annotationType, String attributeName, ForestMethod<?> forestMethod, String source, boolean allowEmptyBrace, MappingParameter[] parameters) {
        this(type, annotationType, attributeName, forestMethod, source, parameters, allowEmptyBrace, 0, source == null ? 0 : source.length());
    }

    public MappingTemplate(int type, Class<? extends Annotation> annotationType, String attributeName, ForestMethod<?> forestMethod, String source, MappingParameter[] parameters, int startReadIndex, int endReadIndex) {
        this(type, annotationType, attributeName, forestMethod, source, parameters, true, startReadIndex, endReadIndex);
    }

    public MappingTemplate(int type, Class<? extends Annotation> annotationType, String attributeName, ForestMethod<?> forestMethod, String source, MappingParameter[] parameters, boolean allowEmptyBrace, int startReadIndex, int endReadIndex) {
        this.type = type;
        this.annotationType = annotationType;
        this.attributeName = attributeName;
        this.forestMethod = forestMethod;
        this.source = source;
        this.parameters = parameters;
        this.startReadIndex = startReadIndex;
        this.endReadIndex = endReadIndex;
        if (this.source == null) {
            this.source = "";
        }
        compile(new CompileContext(allowEmptyBrace));
    }

    public int getType() {
        return type;
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
    
    public void compile(final CompileContext compileContext) {
        context.readIndex = -1 + startReadIndex;
        exprList = new LinkedList<>();
        final StringBuilder buffer = new StringBuilder();
        int startIndex = context.readIndex;
        boolean skip;
        
        while (!isEnd()) {
            skip = false;
            final char ch = nextChar();
            switch (ch) {
                case '$': {
                    final char ch1 = watch(1);
                    if (ch1 == '{') {
                        nextChar();
                        if (buffer.length() > 0) {
                            MappingString str = new MappingString(buffer.toString(), startIndex, startIndex + buffer.length());
                            startIndex = context.readIndex;
                            exprList.add(str);
                        }

                        buffer.setLength(0);
                        final MappingExpr expr = parseExpression(compileContext);
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
                        final MappingString str = new MappingString(buffer.toString(), startIndex, startIndex + buffer.length());
                        startIndex = context.readIndex;
                        exprList.add(str);
                    }
                    final int oldIndex = context.readIndex;
                    buffer.setLength(0);
                    MappingExpr expr = null;
                    try {
                        expr = parseExpression(compileContext);
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
                        buffer.setLength(0);
                        MappingExpr expr = parseProperty(compileContext);
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
                    switch (nc) {
                        case '$':
                        case '{':
                        case '}':
                            buffer.append(nc);
                            nextChar();
                            skip = true;
                            break;
                    }
                }
            }
            if (!skip) {
                buffer.append(ch);
            }
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
        throwSyntaxError("Template Expression Parse Error:\n Character '" + ch +
                "', column " + (context.readIndex + 2) + " at \"" + source + "\"");
    }


    private void syntaxErrorWatchN(char ch, int n) {
        throwSyntaxError("Template Expression Parse Error:\n Character '" + ch +
                "', column " + (context.readIndex + n + 1) + " at \"" + source + "\"");
    }

    public MappingProperty parsePropertyId() {
        char ch = watch(1);
        int startIndex = context.readIndex + 1;
        final StringBuilder builder = new StringBuilder();
        if (Character.isAlphabetic(ch) || ch == '_') {
            do {
                builder.append(ch);
                nextChar();
                ch = watch(1);
            } while (Character.isAlphabetic(ch) ||
                    Character.isDigit(ch) ||
                    ch == '_' ||
                    ch == '-' ||
                    ch == '.' ||
                    ch == '[' ||
                    ch == ']');
        }
        final int endIndex = context.readIndex + 1;
        final String text = builder.toString();
        return new MappingProperty(this, text, startIndex, endIndex);
    }

    public MappingExpr parseProperty(CompileContext compileContext) {
        MappingExpr expr = parsePropertyId();

        skipSpaces();

        char ch = watch(1);
        switch (ch) {
            case '!':
                expr.deepReference = false;
                nextChar();
                break;
            case '?':
                nextChar();
                ch = watch(1);
                if (ch == '?') {
                    nextChar();
                    expr = parseElvisExpr(compileContext, expr, expr.startIndex);
                }
                break;
        }
        return expr;
    }

    public MappingExpr parseExpression(final CompileContext compileContext) {
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
                        if (compileContext.allowEmptyBraces) {
                            return new MappingIndex(this, ++context.argumentIndex, startIndex, context.readIndex + 1);
                        } else {
                            return new MappingString("{}", startIndex, context.readIndex + 1);
                        }
                    }
                    if (expr instanceof MappingInteger) {
                        final int idx = ((MappingInteger) expr).getNumber();
                        context.argumentIndex = idx;
                        return new MappingIndex(this, context.argumentIndex, startIndex, context.readIndex + 1);
                    }
                    if (expr instanceof MappingIdentity) {
                        return new MappingReference(this, (MappingIdentity) expr);
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
                case '#':
                    nextChar();
                    expr = parsePropertyId();
                    break;
                case '?':
                    nextChar();
                    ch = watch(1);
                    if (ch == '.') {
                        nextChar();
                        expr = parseOptionalDot(expr, startIndex);
                    } else if (ch == '?') {
                        nextChar();
                        expr = parseElvisExpr(compileContext, expr, startIndex);
                    } else if (expr != null) {
                        expr = parseOptionExpr(expr, startIndex);
                    } else {
                        syntaxErrorWatch1(ch);
                    }
                    break;
                case '!':
                    nextChar();
                    if (expr != null) {
                        expr.deepReference = false;
                    } else {
                        syntaxErrorWatch1(ch);
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
                    expr = parseInvokeParams(compileContext, expr, methodName);
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
            throwSyntaxError("There is no valid token before '.'");
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
            throwSyntaxError("There is no valid token before '?.'");
        }
        final MappingIdentity id = parseIdentity();
        return new MappingOptionalDot(this, newExpr, id, startIndex, id.endIndex);
    }

    public MappingElvisExpr parseElvisExpr(final CompileContext compileContext, final MappingExpr leftExpr, final int startIndex) {
        final MappingExpr newExpr = leftExpr instanceof MappingIdentity ?
                new MappingReference(
                        this,
                        ((MappingIdentity) leftExpr).getName(),
                        true,
                        leftExpr.startIndex, leftExpr.endIndex) :
                leftExpr;
        if (newExpr == null) {
            throw new ForestExpressionException("Unexpected token '??'", annotationType, attributeName, forestMethod, this, context.readIndex - 1, context.readIndex + 1);
        }
        final MappingExpr rightExpr = parseExpression(compileContext);
        if (rightExpr == null) {
            throw new ForestExpressionException("Unexpected token '??'", annotationType, attributeName, forestMethod, this, context.readIndex - 1, context.readIndex + 1);
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
            throwSyntaxError("There is no valid token before '?'");
        }
        return newExpr;
    }
    

    public MappingIdentity parseIdentity() {
        MappingExpr expr = parseTextToken();
        matchToken(expr, Token.ID);
        return (MappingIdentity) expr;
    }



    public MappingExpr parseTextToken() {
        char ch = watch(1);
        int startIndex = context.readIndex + 1;
        final StringBuilder builder = new StringBuilder();
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
        final StringBuilder builder = new StringBuilder();
        char ch = watch(1);
        int startIndex = context.readIndex + 1;
        while (ch != quoteChar && !isEnd()) {
            builder.append(ch);
            nextChar();
            ch = watch(1);
        }
        int endIndex = context.readIndex + 1;
        if (isTemplateString) {
            return new MappingTemplateString(this, source, startIndex, endIndex);
        }
        return new MappingString(builder.toString(), startIndex, endIndex);
    }


    public MappingInvoke parseInvokeParams(final CompileContext compileContext, MappingExpr left, MappingIdentity name) {
        return parseMethodParams_inner(compileContext, left, name, new ArrayList<>());
    }


    public MappingInvoke parseMethodParams_inner(final CompileContext compileContext, MappingExpr left, MappingIdentity name, List<MappingExpr> argExprList) {
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
                MappingExpr expr = parseExpression(compileContext);
                argExprList.add(expr);
                return parseMethodParams_inner(compileContext, left, name, argExprList);
            default:
                MappingExpr expr2 = parseExpression(compileContext);
                if (expr2 == null) {
                    if (argExprList.isEmpty()) {
                        throwSyntaxError("Expecting some argument expression between '(' and ')'");
                        return null;
                    }
                } else {
                    argExprList.add(expr2);
                    return parseMethodParams_inner(compileContext, left, name, argExprList);
                }
        }
        throwSyntaxError("Expecting token ')'");
        return null;
    }



    public MappingExpr parseLiteral() {
        char ch = watch(1);
        if (Character.isDigit(ch)) {
            final StringBuilder builder = new StringBuilder();
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

    protected Object postValueRender(Object value, boolean enable) {
        return value;
    }

    protected String renderExpression(VariableScope scope, ForestJsonConverter jsonConverter, MappingExpr expr, Object[] args, boolean allowEmptyBraces) {
        return renderExpression(scope, jsonConverter, expr, args, allowEmptyBraces, false);
    }

    protected String renderExpression(VariableScope scope, ForestJsonConverter jsonConverter, MappingExpr expr, Object[] args, boolean allowEmptyBraces, boolean enablePostValueRender) {
        Object val = null;
        MappingParameter param = null;
        if (expr instanceof MappingString) {
            return ((MappingString) expr).getText();
        } else if (expr instanceof MappingIndex) {
            if (!allowEmptyBraces) {
                final String indexExprText = expr.toTemplateString();
                if (indexExprText.length() == 2) {
                    return indexExprText;
                }
            }
            Integer index = ((MappingIndex) expr).getIndex();
            if (parameters != null && index < parameters.length) {
                param = parameters[index];
            }
            if (args != null && index < args.length) {
                val = postValueRender(args[index], enablePostValueRender);
            }
            if (val != null) {
                val = getParameterValue(jsonConverter, postValueRender(val,enablePostValueRender));
                if (param != null && param.isUrlEncode()) {
                    val = URLUtils.queryValueEncode(String.valueOf(val), param.getCharset());
                }
                return String.valueOf(val);
            }
        } else {
            val = expr.render(scope, args);
            if (val != null) {
                val = getParameterValue(jsonConverter, postValueRender(val, enablePostValueRender));
                if (param != null && param.isUrlEncode()) {
                    val = URLUtils.pathEncode(String.valueOf(val), param.getCharset());
                }
            }
            return String.valueOf(val);
        }
        return null;
    }

    public String render(VariableScope scope, Object[] args) {
        return render(scope, args, true);
    }

    public String render(VariableScope scope, Object[] args, boolean allowEmptyBrace) {
        final ForestJsonConverter jsonConverter = scope.getConfiguration().getJsonConverter();
        int len = exprList.size();
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            MappingExpr expr = exprList.get(i);
            String val = renderExpression(scope, jsonConverter, expr, args, allowEmptyBrace);
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
        final StringBuilder builder = new StringBuilder();
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
        final StringBuilder builder = new StringBuilder();
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
        MappingTemplate template = new MappingTemplate(
                type, annotationType, attributeName, forestMethod, this.source, this.parameters );
        template.exprList = this.exprList;
        return template;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        for (MappingExpr expr : exprList) {
            buff.append(expr.toString());
        }
        return buff.toString();
    }

    public MappingTemplate valueOf(String value) {
        return new MappingTemplate(
                type, annotationType, attributeName, forestMethod, value, forestMethod.getParameters());
    }


}
