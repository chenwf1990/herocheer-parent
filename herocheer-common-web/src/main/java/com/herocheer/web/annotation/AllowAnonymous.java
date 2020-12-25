package com.herocheer.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @desc 不用登录校验
 * @author chenwf
 * @create 2020/12/25
 * @company 厦门熙重电子科技有限公司
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowAnonymous {
}
