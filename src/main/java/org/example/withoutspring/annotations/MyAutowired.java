package org.example.withoutspring.annotations;

import java.lang.annotation.*;

/**
 * @author fengyadong
 * @date 2022/5/26 11:24
 * @Description
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    String value() default "";
}
