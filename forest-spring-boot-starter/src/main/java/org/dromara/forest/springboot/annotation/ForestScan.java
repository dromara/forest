package org.dromara.forest.springboot.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author guihuo   (E-mail:1620657419@qq.com)
 * @since 2018-09-25 11:58
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ForestScannerRegister.class})
public @interface ForestScan {

    String[] value() default {};

    String configuration() default "";

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

}
