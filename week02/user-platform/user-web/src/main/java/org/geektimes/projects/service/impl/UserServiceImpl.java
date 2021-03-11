package org.geektimes.projects.service.impl;

import java.util.Set;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.geektimes.projects.domain.User;
import org.geektimes.projects.respository.UserRepository;
import org.geektimes.projects.service.UserService;

public class UserServiceImpl implements UserService {

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    @Resource(name = "bean/UserRepository")
    private UserRepository userRepository;

    @Resource(name = "bean/Validator")
    private Validator validator;

    @Override
    public boolean register(User user) {

//        entityManager.persist(user);
        if (!isValid(user)) {
            return false;
        }

        return userRepository.save(user);

//        return true;
    }

    @Override
    public User login(User user) {
        return userRepository.getByEmailAndPassword(user.getEmail(), user.getPassword());
    }


    private boolean isValid(User user) {
        Set<ConstraintViolation<User>> violations =  validator.validate(user);
        if (violations.size() > 0) {
            return false;
        }

        return true;
    }
}
