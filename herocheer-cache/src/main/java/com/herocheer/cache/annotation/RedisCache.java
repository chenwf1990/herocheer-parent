package com.herocheer.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chenwf
 * @desc
 * @date 2021/2/2
 * @company 厦门熙重电子科技有限公司
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {
    String key() default "";

    int expire() default 0;
}
