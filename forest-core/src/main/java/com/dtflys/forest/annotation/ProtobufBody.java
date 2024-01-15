package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.BinaryBodyLifeCycle;
import com.dtflys.forest.lifecycles.parameter.ProtobufBodyLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Protobuf格式请求体注解
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.5
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface ProtobufBody {

}
