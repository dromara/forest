package com.dtflys.forest.converter.json;


import com.dtflys.forest.annotation.BodyType;

import java.lang.annotation.*;

@BodyType(type = "json", encoder = ForestFastjson2Converter.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Fastjson2Encoder {
}
