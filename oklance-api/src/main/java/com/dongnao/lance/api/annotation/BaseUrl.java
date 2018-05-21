package com.dongnao.lance.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lance
 * @date 2018/5/20
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface BaseUrl {

    String value();
}
