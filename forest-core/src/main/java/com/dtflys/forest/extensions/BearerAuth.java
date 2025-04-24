package com.dtflys.forest.extensions;

import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.annotation.RequestAttributes;
import com.dtflys.forest.lifecycles.authorization.BasicAuthLifeCycle;
import com.dtflys.forest.lifecycles.authorization.BearerAuthLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bearer Auth 类型的验签注解
 * <p>该注解可以修饰接口类和方法，可以将{@code token()}添加到请求头上</p>
 * <p>该注解的生命周期类为 {@link BearerAuthLifeCycle}</p>
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @see BearerAuthLifeCycle
 * @since 1.6.5
 */
@Documented
@MethodLifeCycle(BearerAuthLifeCycle.class)
@RequestAttributes
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface BearerAuth {

    /**
     * Basic Auth 验证的用户名
     *
     * @return 用户名
     */
    String token();

}
