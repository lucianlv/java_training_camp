package pers.cocoadel.user.platform.validator.bean.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserValidAnnotationValidator.class)
public @interface UserValid {

    String message() default "用户数据不合法";

    Class<?>[]groups() default {};

    Class<? extends Payload>[]payload() default {};

    int idRange() default 1;

    int passwordMinLen() default 6;

    int passwordMaxLen() default 32;
}
