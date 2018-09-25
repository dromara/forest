package com.dtflys.forest.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author guihuo   (E-mail:1620657419@qq.com)
 * @since 2018-09-25 11:58
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ForestScannerRegistrar.class})
public @interface ForestScan {

    String[] value() default {};

    String configuration() default "";

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

}
