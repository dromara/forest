package com.dtflys.forest.http;

import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.Validations;

import java.util.function.Consumer;
import java.util.function.Function;

public class ForestRequestConditionWrapper<T> {

    private final ForestRequest<T> request;

    private boolean endIf = false;

    private boolean condition;


    public ForestRequestConditionWrapper(ForestRequest<T> request, boolean condition) {
        this.request = request;
        this.condition = condition;
    }

    public ForestRequest<?> getRequest() {
        return request;
    }

/*
    public ForestRequestConditionWrapper<T> ifThen(boolean condition, Consumer<ForestRequest<?>> consumer) {
        return request.ifThen(condition, consumer);
    }


    public ForestRequestConditionWrapper<T> ifThen(Function<ForestRequest<?>, Boolean> conditionFunc, Consumer<ForestRequest<?>> consumer) {
        return request.ifThen(conditionFunc, consumer);
    }
*/


    public ForestRequestConditionWrapper<T> elseIfThen(boolean condition, Consumer<ForestRequest<?>> consumer) {
        if (!endIf && !this.condition && condition) {
            consumer.accept(request);
            endIf = true;
        }
        this.condition = condition;
        return this;
    }


    public ForestRequestConditionWrapper<T> elseIfThen(Function<ForestRequest<?>, Boolean> conditionFunc, Consumer<ForestRequest<?>> consumer) {
        Validations.assertParamNotNull(conditionFunc, "conditionFunc");
        return elseIfThen(conditionFunc.apply(request), consumer);
    }

    public ForestRequestConditionWrapper<T> elseIfNullThen(Object value, Consumer<ForestRequest<?>> consumer) {
        return elseIfThen(value == null, consumer);
    }

    public ForestRequestConditionWrapper<T> elseIfNotNullThen(Object value, Consumer<ForestRequest<?>> consumer) {
        return elseIfThen(value != null, consumer);
    }

    public ForestRequestConditionWrapper<T> elseIfEmptyThen(CharSequence value, Consumer<ForestRequest<?>> consumer) {
        return elseIfThen(StringUtils.isEmpty(value), consumer);
    }

    public ForestRequestConditionWrapper<T> elseIfNotEmptyThen(CharSequence value, Consumer<ForestRequest<?>> consumer) {
        return elseIfThen(StringUtils.isNotEmpty(value), consumer);
    }

    public ForestRequest<T> elseThen(Consumer<ForestRequest<?>> consumer) {
        if (!endIf && !this.condition) {
            consumer.accept(request);
        }
        return request;
    }

    public ForestRequest<T> endIf() {
        return request;
    }

}
