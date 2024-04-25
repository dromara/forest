package com.dtflys.forest.http;

import com.dtflys.forest.utils.Validations;

import java.util.function.Consumer;
import java.util.function.Function;

public class ForestRequestConditionWrapper<T> {

    private final ForestRequest<T> request;

    private boolean endIf = false;

    private Boolean condition;


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
        if (!endIf && this.condition != null && !this.condition && condition) {
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


    public ForestRequest<T> elseThen(Consumer<ForestRequest<?>> consumer) {
        if (!endIf && condition != null && !this.condition) {
            consumer.accept(request);
        }
        return request;
    }

    public ForestRequest<T> endIf() {
        return request;
    }

}
