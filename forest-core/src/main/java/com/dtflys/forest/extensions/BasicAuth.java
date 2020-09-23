package com.dtflys.forest.extensions;

import com.dtflys.forest.annotation.RequestAttributes;
import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.lifecycles.authorization.BasicAuthLifeCycle;

import java.lang.annotation.*;

/**
 * Basic Auth 类型的验签注解
 * <p>该注解可以修饰接口类和方法，可以将{@code username()}和{@code password()}属性进行加密,
 * 并添加到请求头上</p>
 * <p>该注解的生命周期类为 {@link BasicAuthLifeCycle}</p>
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @see BasicAuthLifeCycle
 */
@Documented
@MethodLifeCycle(BasicAuthLifeCycle.class)
@RequestAttributes
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface BasicAuth {

    /** Basic auth username */
    String username();

    /** Basic auth password */
    String password();
}
