package com.thebeastshop.forest.springboot.annotation;

import com.dtflys.forest.springboot.ForestAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ForestAutoConfiguration.class})
public @interface EnableForest {
}
