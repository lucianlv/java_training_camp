package org.geektimes.projects.user.validator.bean.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Auther: liuyj
 * @Date: 2021/03/09/16:39
 * @Description: 手机号校验
 */
@Documented
@Constraint(
        validatedBy = MobileAnnotationValidator.class   //校验类
)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Moblie {

    String message() default "手机号格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
