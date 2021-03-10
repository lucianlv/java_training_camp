package org.geektimes.projects.service.impl;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import org.geektimes.projects.domain.User;
import org.geektimes.projects.respository.UserRepository;
import org.geektimes.projects.service.UserService;

public class UserServiceImpl implements UserService {

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    @Resource(name = "bean/UserRepository")
    private UserRepository userRepository;

    @Override
    public boolean register(User user) {

//        entityManager.persist(user);
        return userRepository.save(user);

//        return true;
    }
}
