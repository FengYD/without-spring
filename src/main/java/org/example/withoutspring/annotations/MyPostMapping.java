package org.example.withoutspring.annotations;

import java.lang.annotation.*;

/**
 * @author fengyadong
 * @date 2022/5/26 11:27
 * @Description
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyPostMapping {
    String value() default "";
}
