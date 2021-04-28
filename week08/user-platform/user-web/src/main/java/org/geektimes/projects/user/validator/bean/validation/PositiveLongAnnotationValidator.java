package org.geektimes.projects.user.validator.bean.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Auther: liuyj
 * @Date: 2021/03/09/17:22
 * @Description:正整数
 */
public class PositiveLongAnnotationValidator implements ConstraintValidator<PositiveLong,Long> {
    @Override
    public void initialize(PositiveLong constraintAnnotation) {

    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        return value!=null && value>0;
    }


}
