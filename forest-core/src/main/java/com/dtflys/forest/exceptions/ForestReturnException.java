package com.dtflys.forest.exceptions;

/**
 * Forest返回类型异常
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
public class ForestReturnException extends ForestRuntimeException {

    /**
     * ReturnType注解所修饰的参数的类型
     */
    private final Class<?> classOfReturnTypeParameter;

    public ForestReturnException(Class<?> classOfReturnTypeParameter) {
        super("[Forest] parameter type '" + classOfReturnTypeParameter.getName() + "' is not supported for annotation @Return");
        this.classOfReturnTypeParameter = classOfReturnTypeParameter;
    }

    /**
     * 获取ReturnType注解所修饰的参数的类型
     *
     * @return {@link Class}实例
     */
    public Class<?> getClassOfReturnTypeParameter() {
        return classOfReturnTypeParameter;
    }
}
