package org.geektimes.projects.respository.impl;

import org.geektimes.projects.domain.User;
import org.geektimes.projects.respository.UserRepository;

public class DatabaseUserRepository implements UserRepository {

    @Override
    public boolean save(User user) {
        return false;
    }

    @Override
    public boolean deleteById(Long userId) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User getByEmailAndPassword(String email, String password) {
        return null;
    }
}
