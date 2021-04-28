package org.geektimes.projects.user.validator.bean.validation;

import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * @Auther: liuyj
 * @Date: 2021/03/09/16:41
 * @Description:手机号校验
 */
public class MobileAnnotationValidator implements ConstraintValidator<Moblie,String> {
    @Override
    public void initialize(Moblie constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotEmpty(value)) {
            return validateMobilePhone(value);
        } else {
            return false;
        }
    }

    public static boolean validateMobilePhone(String in) {
        Pattern pattern = Pattern.compile("^[1]\\d{10}$");
        return pattern.matcher(in).matches();
    }

}
