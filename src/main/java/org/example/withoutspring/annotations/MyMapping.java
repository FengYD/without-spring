package org.example.withoutspring.annotations;
import java.lang.annotation.*;
/**
 * @author fengyadong
 * @date 2022/5/26 11:25
 * @Description
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyMapping {
    String value() default "";
}
