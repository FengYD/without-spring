package org.example.withoutspring.annotations;

import java.lang.annotation.*;
/**
 * @author fengyadong
 * @date 2022/5/26 11:26
 * @Description
 */


@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {
    String value() default "";
}
