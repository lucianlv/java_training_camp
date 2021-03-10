package org.geektimes.projects.respository;

import org.geektimes.projects.domain.User;

public interface UserRepository {

    boolean save(User user);

    boolean deleteById(Long userId);

    boolean update(User user);

    User getByEmailAndPassword(String email, String password);

}
