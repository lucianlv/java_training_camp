package org.geektimes.projects.validator.bean.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserValidAnnotationValidator.class)
public @interface UserValid {

    String message() default "用户信息不合法";


    int idMin() default 0;

    int passwordMinLen() default 6;

    int passwordMaxLen() default 32;

    int phoneNumberLen() default 0;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
