package org.geektimes.projects.user.validator.bean.validation;

import org.geektimes.projects.user.domain.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.Set;

public class BeanValidationDemo {

    public static void main(String[] args) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        // cache the factory somewhere
        Validator validator = factory.getValidator();

//        User user = new User();
//        user.setId(23233L);
//        user.setPassword("*666666666**");

        User user = new User();
        user.setId(123L);
        user.setName("小马哥");
        user.setPassword("**adfafasf");
        user.setEmail("mercyblitz@gmail.com");
        user.setPhoneNumber("18588871561");
        // 校验结果
        Set<ConstraintViolation<User>> violations = validator.validate(user, Default.class);

        violations.forEach(c -> {
            String property = c.getPropertyPath().toString();
            String message = c.getMessage();
            System.out.println(property+"：  "+message);
        });
    }
}
