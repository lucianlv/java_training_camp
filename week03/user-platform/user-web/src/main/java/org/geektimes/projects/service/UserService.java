package org.geektimes.projects.service;

import org.geektimes.projects.domain.User;

public interface UserService {

    boolean register(User user);

    User login(User user);

}
