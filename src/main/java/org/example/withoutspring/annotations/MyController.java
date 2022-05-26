package org.example.withoutspring.annotations;

import java.lang.annotation.*;
/**
 * @author fengyadong
 * @date 2022/5/26 11:25
 * @Description
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
    String value() default "";
}
