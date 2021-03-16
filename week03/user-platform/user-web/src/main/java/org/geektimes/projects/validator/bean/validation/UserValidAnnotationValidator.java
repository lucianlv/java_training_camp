package org.geektimes.projects.validator.bean.validation;

import java.util.logging.Logger;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.geektimes.projects.domain.User;

public class UserValidAnnotationValidator implements ConstraintValidator<UserValid, User> {

    private Logger logger = Logger.getLogger(UserValidAnnotationValidator.class.getName());

    private int idMin;

    private int passwordMinLen;

    private int passwordMaxLen;

    private int phoneNumLen;

    public void initialize(UserValid annotation) {
        this.idMin = annotation.idMin();
        this.passwordMinLen = annotation.passwordMinLen();
        this.passwordMaxLen = annotation.passwordMaxLen();
        this.phoneNumLen = annotation.phoneNumberLen();
    }

    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {

        // 获取模板信息
//        String msg = context.getDefaultConstraintMessageTemplate();
//        logger.info(msg);

        if (value.getPassword() == null || value.getPassword().length() < this.passwordMinLen || value.getPassword().length() > this.passwordMaxLen) {
            return false;
        }

        logger.info(value.toString());

        if (value.getPhoneNumber() == null || !isNumeric(value.getPhoneNumber()) || value.getPhoneNumber().length() != this.phoneNumLen) {
            return false;
        }

        return true;
    }

    public boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }
}
