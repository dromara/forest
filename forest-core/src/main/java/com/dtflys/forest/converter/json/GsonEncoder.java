package com.dtflys.forest.converter.json;


import com.dtflys.forest.annotation.BodyType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@BodyType(type = "json", encoder = ForestGsonConverter.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface GsonEncoder {
}
