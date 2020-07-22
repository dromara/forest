package com.thebeastshop.forest.springboot.annotation;

import com.thebeastshop.forest.springboot.ForestAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ForestAutoConfiguration.class})
public @interface EnableForest {
}
