package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.exceptions.ForestExpressionNullException;
import com.dtflys.forest.exceptions.ForestIndexReferenceException;
import com.dtflys.forest.exceptions.ForestReferenceException;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.TemplateUtils;

/**
 * @author gongjun
 * @since 2016-05-04
 */
public abstract class MappingExpr {

    protected final MappingTemplate template;

    boolean deepReference = true;

    final Token token;

    int startIndex = -1;

    int endIndex = -1;

    protected MappingExpr(MappingTemplate template, Token token) {
        this.template = template;
        this.token = token;
    }

    public Object render(VariableScope scope, Object[] args) {
        return null;
    }

    public Object render(VariableScope scope) {
        return render(scope, new Object[0]);
    }


    public abstract boolean isIterateVariable();

    public void setIndexRange(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public ForestMethod<?> getForestMethod() {
        return template.forestMethod;
    }

    public Token getToken() {
        return token;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public String toTemplateString() {
        if (startIndex != -1 && endIndex != -1) {
            return template.source.substring(startIndex, endIndex);
        }
        return template.source;
    }

    protected Object checkDeepReference(Object obj, MappingExpr expr, VariableScope scope, Object[] args) {
        if (deepReference) {
            if (obj instanceof CharSequence) {
                final String str = obj.toString();
                if (str.length() == 2 && str.charAt(0) == '{' && str.charAt(1) == '}') {
                    return str;
                }
                try {
                    return MappingValue.rendered(TemplateUtils.readString(str, scope, args, false));
                } catch (Throwable th) {
                    throwReferenceException(expr, str, th);
                }
            }
            if (obj instanceof MappingTemplate) {
                final MappingTemplate template = (MappingTemplate) obj;
                try {
                    return MappingValue.rendered(template.render(scope, args, false));
                } catch (Throwable th) {
                    throwReferenceException(expr, template.getSource(), th);
                }
            }
        } else {
            if (obj instanceof MappingTemplate) {
                return ((MappingTemplate) obj).getSource();
            }
            return obj;
        }
        return obj;
    }

    protected void throwExpressionException(String message, Throwable th) throws ForestExpressionNullException {
        throw new ForestExpressionException(
                message, template.annotationType, template.attributeName, getForestMethod(), template, startIndex, endIndex, th);
    }

    protected void throwExpressionException(String message, MappingExpr expr, Throwable th) throws ForestExpressionNullException {
        throw new ForestExpressionException(
                message, template.annotationType, template.attributeName, getForestMethod(), template, expr.startIndex, expr.endIndex, th);
    }


    protected void throwVariableUndefinedException(String name) throws ForestVariableUndefinedException {
        throw new ForestVariableUndefinedException(
                template.annotationType, template.attributeName, template.forestMethod, name, template, startIndex, endIndex);
    }

    protected void throwExpressionNulException(String nullVariableName, MappingExpr expr, Throwable cause) throws ForestExpressionNullException {
        throw new ForestExpressionNullException(
                template.annotationType, template.attributeName, template, nullVariableName, expr, cause);
    }

    protected void throwIndexReferenceException(int index, int length) throws ForestExpressionNullException {
        throw new ForestIndexReferenceException(
                template.annotationType, template.attributeName, template.forestMethod, index, length, template, startIndex, endIndex);
    }

    protected void throwReferenceException(MappingExpr expr, Throwable cause) throws ForestExpressionNullException {
        if (cause instanceof ForestExpressionException) {
            throwReferenceException(expr, ((ForestExpressionException) cause).getSource(), cause);
        }
        throw new ForestReferenceException(
                 template.annotationType, template.attributeName, template.forestMethod, template, expr, startIndex, endIndex, cause);
    }

    protected void throwReferenceException(MappingExpr expr, String refTemplate, Throwable cause) throws ForestExpressionNullException {
        throw new ForestReferenceException(
                template.annotationType, template.attributeName, template.forestMethod, template, refTemplate, expr, startIndex, endIndex, cause);
    }



}
