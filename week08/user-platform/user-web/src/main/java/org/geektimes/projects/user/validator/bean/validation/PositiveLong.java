package org.geektimes.projects.user.validator.bean.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Auther: liuyj
 * @Date: 2021/03/09/16:39
 * @Description: 正整数
 */
@Documented
@Constraint(
        validatedBy = PositiveLongAnnotationValidator.class   //校验类
)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveLong {

    String message() default "必须是大于 0 的整数";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
