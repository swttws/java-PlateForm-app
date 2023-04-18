package com.su.aop;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Resubmit {

    String value() default "";//注解默认标识

    long lockTime() default 5;//过期时间

}
