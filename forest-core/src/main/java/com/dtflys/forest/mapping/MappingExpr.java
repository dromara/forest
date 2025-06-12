package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.exceptions.ForestExpressionNullException;
import com.dtflys.forest.exceptions.ForestIndexReferenceException;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.reflection.ForestMethod;

/**
 * @author gongjun
 * @since 2016-05-04
 */
public abstract class MappingExpr {

    protected final MappingTemplate source;

    final Token token;

    int startIndex = -1;

    int endIndex = -1;

    protected MappingExpr(MappingTemplate source, Token token) {
        this.source = source;
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
        return source.forestMethod;
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
            return source.template.substring(startIndex, endIndex);
        }
        return source.template;
    }
    

    protected void throwExpressionException(String message, Throwable th) throws ForestExpressionNullException {
        throw new ForestExpressionException(
                message, source.annotationType, source.attributeName, getForestMethod(), source.template, startIndex, endIndex, th);
    }

    protected void throwExpressionException(String message, MappingExpr expr, Throwable th) throws ForestExpressionNullException {
        throw new ForestExpressionException(
                message, source.annotationType, source.attributeName, getForestMethod(), source.template, expr.startIndex, expr.endIndex, th);
    }


    protected void throwVariableUndefinedException(String name) throws ForestVariableUndefinedException {
        throw new ForestVariableUndefinedException(
                source.annotationType, source.attributeName, source.forestMethod, name, source.template, startIndex, endIndex);
    }

    protected void throwExpressionNulException(String nullVariableName, MappingExpr expr, Throwable cause) throws ForestExpressionNullException {
        throw new ForestExpressionNullException(
                source.annotationType, source.attributeName, source.template, nullVariableName, expr, cause);
    }

    protected void throwIndexReferenceException(int index, int length) throws ForestExpressionNullException {
        throw new ForestIndexReferenceException(
                source.annotationType, source.attributeName, source.forestMethod, index, length, source.template, startIndex, endIndex);
    }

}
