package pers.cocoadel.user.platform.repository;

import org.apache.commons.collections.CollectionUtils;
import pers.cocoadel.user.platform.domain.User;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class DBJPAUserRepository implements UserRepository {

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    @Override
    public boolean save(final User user) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        transaction.commit();
        return true;
    }

    @Override
    public boolean deleteById(final Long userId) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        entityManager.remove(user);
        return true;
    }

    @Override
    public boolean update(final User user) {
        return save(user);
    }

    @Override
    public User getById(final Long userId) {
        return entityManager.find(User.class, userId);
    }

    @Override
    public User getByNameAndPassword(final String userName,final String password) {
        String jpql = String.format("select u from User u where u.name = '%s' and u.password = '%s'",
                userName, password);
        List<User> list = entityManager.createQuery(jpql, User.class).getResultList();
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public User getByName(String userName) {
        String jpql = String.format("select u from User u where u.name = '%s'", userName);
        List<User> list = entityManager.createQuery(jpql, User.class).getResultList();
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public Collection<User> getAll() {
        String jpql = "select u from User u";
        return entityManager.createQuery(jpql, User.class).getResultList();
    }

    private <T, R> R execute(Function<T, R> function, T t) {
        try {
            return function.apply(t);
        } catch (Throwable throwable) {
            entityManager.flush();
            throw throwable;
        }
    }

    private <R> R execute(Supplier<R> supplier) {
        try {
            return supplier.get();
        } catch (Throwable throwable) {
            entityManager.flush();
            throw throwable;
        }
    }
}
