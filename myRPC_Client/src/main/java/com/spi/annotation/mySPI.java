package com.spi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 13:42 2020/1/1
 * @Modified By:
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface mySPI {
    String value() default "";
}
