package pers.cocoadel.user.platform.validator.bean.validation;

import org.apache.commons.lang.StringUtils;
import pers.cocoadel.user.platform.domain.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidAnnotationValidator implements ConstraintValidator<UserValid, User> {

    /**
     * 电话号码校正
     */
    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    private int idRange;

    private int passwordMinLen;

    private int passwordMaxLen;

    public void initialize(UserValid annotation) {
        this.idRange = annotation.idRange();
        this.passwordMaxLen = annotation.passwordMaxLen();
        this.passwordMinLen = annotation.passwordMinLen();
    }

    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {
        //用户 ID 范围检测
        if (value.getId() < idRange) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("用户 Id 必须大于 0").addConstraintViolation();
            return false;
        }

        //用户密码长度检测
        int passwordLen = StringUtils.isBlank(value.getPassword()) ? 0 : value.getPassword().length();
        if (passwordLen < passwordMinLen || passwordLen > passwordMaxLen) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("密码长度必须在 6-32 位之间").addConstraintViolation();
            return false;
        }

        if (!isMobile(value.getPhoneNumber())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("手机号码格式错误").addConstraintViolation();
            return false;
        }
        return true;
    }

    public boolean isMobile(String src) {
        if (StringUtils.isEmpty(src)) {
            return false;
        }
        Matcher m = mobile_pattern.matcher(src);
        return m.matches();
    }

}
