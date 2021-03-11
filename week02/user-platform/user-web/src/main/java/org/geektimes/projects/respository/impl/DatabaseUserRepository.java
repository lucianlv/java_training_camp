package org.geektimes.projects.respository.impl;

import static org.apache.commons.lang.ClassUtils.wrapperToPrimitive;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import org.geektimes.web.mvc.function.ThrowableAction;
import org.geektimes.web.mvc.function.ThrowableFunction;
import org.geektimes.projects.domain.User;
import org.geektimes.projects.respository.UserRepository;
import org.geektimes.projects.sql.DBConnectionManager;

public class DatabaseUserRepository implements UserRepository {

    private static Logger logger = Logger.getLogger(DatabaseUserRepository.class.getName());

    @Resource(name = "bean/DBConnectionManager")
    private DBConnectionManager dbConnectionManager;

    /**
     * 通用处理方式
     */
    private static Consumer<Throwable> COMMON_EXCEPTION_HANDLER = e -> logger.log(Level.SEVERE, e.getMessage());

    public static final String INSERT_USER_DML_SQL =
        "INSERT INTO users(name,password,email,phoneNumber) VALUES " +
            "(?,?,?,?)";

    public static final String QUERY_ALL_USERS_DML_SQL = "SELECT id,name,password,email,phoneNumber FROM users";

    @Override
    public boolean save(User user) {
        Connection connection = getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_DML_SQL);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPhoneNumber());
            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e.getCause());
        }

        return true;
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
        Connection connection = getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                QUERY_ALL_USERS_DML_SQL + " WHERE email=? AND password=?");
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setPhoneNumber(resultSet.getString("phoneNumber"));
                return user;
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e.getCause());
        }

        return null;
    }

    /**
     * @param sql
     * @param function
     * @param <T>
     * @return
     */
    protected <T> T executeQuery(String sql, ThrowableFunction<ResultSet, T> function,
        Consumer<Throwable> exceptionHandler, Object... args) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Class argType = arg.getClass();

                Class wrapperType = wrapperToPrimitive(argType);

                if (wrapperType == null) {
                    wrapperType = argType;
                }

                // Boolean -> boolean
                String methodName = preparedStatementMethodMappings.get(argType);
                Method method = PreparedStatement.class.getMethod(methodName, wrapperType);
                method.invoke(preparedStatement, i + 1, args);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            // 返回一个 POJO List -> ResultSet -> POJO List
            // ResultSet -> T
            return function.apply(resultSet);
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }
        return null;
    }


    private Connection getConnection() {
        return dbConnectionManager.getConnection();
    }

    /**
     * 数据类型与 ResultSet 方法名映射
     */
    static Map<Class, String> resultSetMethodMappings = new HashMap<>();

    static Map<Class, String> preparedStatementMethodMappings = new HashMap<>();

    static {
        resultSetMethodMappings.put(Long.class, "getLong");
        resultSetMethodMappings.put(String.class, "getString");

        preparedStatementMethodMappings.put(Long.class, "setLong"); // long
        preparedStatementMethodMappings.put(String.class, "setString"); //


    }
}
