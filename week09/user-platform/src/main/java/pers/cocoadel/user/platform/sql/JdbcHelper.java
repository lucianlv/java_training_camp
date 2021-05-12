package pers.cocoadel.user.platform.sql;

import org.apache.commons.lang.exception.ExceptionUtils;
import pers.cocoadel.context.function.ThrowableFunction;

import javax.annotation.Resource;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.ClassUtils.wrapperToPrimitive;

public class JdbcHelper {

    private static final Logger logger = Logger.getLogger(JdbcHelper.class.getName());

    /**
     * 通用处理方式
     */
    private static final Consumer<Throwable> COMMON_EXCEPTION_HANDLER = e -> logger.log(Level.SEVERE,
            ExceptionUtils.getFullStackTrace(e));

    /**
     * 数据类型与 ResultSet 方法名映射
     */
    static Map<Class<?>, String> resultSetMethodMappings = new HashMap<>();

    static Map<Class<?>, String> preparedStatementMethodMappings = new HashMap<>();

    static {
        resultSetMethodMappings.put(Long.class, "getLong");
        resultSetMethodMappings.put(String.class, "getString");

        preparedStatementMethodMappings.put(Long.class, "setLong"); // long
        preparedStatementMethodMappings.put(String.class, "setString"); //
    }

    @Resource(name = "bean/DBConnectionManager")
    private DBConnectionManager dbConnectionManager;

    public JdbcHelper(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    public JdbcHelper() {

    }


    private Connection getConnection() {
        return dbConnectionManager.getConnection();
    }

    public <T> Collection<T> queryAll(String sql, Class<T> returnType, Object... args) {
        List<T> result = executeQuery(sql, resultSet -> {
            List<T> list = new ArrayList<>();
            // 如果存在并且游标滚动 // SQLException
            while (resultSet.next()) {
                T t = resultSetToPojo(resultSet, returnType);
                list.add(t);
            }
            return list;
        }, COMMON_EXCEPTION_HANDLER, args);
        return result == null ? Collections.emptyList() : result;
    }

    public <T> T queryOne(String sql, Class<T> returnType, Object... args) {
        return executeQuery(sql, resultSet -> resultSet.next() ? resultSetToPojo(resultSet, returnType) : null,
                COMMON_EXCEPTION_HANDLER, args);
    }

    public int executeUpdate(String sql, Object... args) {
        return executeUpdate(sql, COMMON_EXCEPTION_HANDLER, args);
    }


    protected int executeUpdate(String sql, Consumer<Throwable> exceptionHandler, Object... args) {
        try {
            Connection connection = getConnection();
            if (connection == null) {
                throw new NullPointerException("");
            }
            PreparedStatement preparedStatement = createPreparedStatement(connection, sql, args);
            return preparedStatement.executeUpdate();
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }
        return 0;
    }

    protected <T> T executeQuery(String sql, ThrowableFunction<ResultSet, T> function,
                                 Consumer<Throwable> exceptionHandler, Object... args) {
        try {
            Connection connection = getConnection();
            if (connection == null) {
                throw new NullPointerException("");
            }
            PreparedStatement preparedStatement = createPreparedStatement(connection, sql, args);
            ResultSet resultSet = preparedStatement.executeQuery();
            // 返回一个 POJO List -> ResultSet -> POJO List
            // ResultSet -> T
            return function.apply(resultSet);
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }
        return null;
    }

    private PreparedStatement createPreparedStatement(Connection connection, String sql, Object... args)
            throws Throwable {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Class<?> argType = arg.getClass();
            Class<?> wrapperType = wrapperToPrimitive(argType);
            if (wrapperType == null) {
                wrapperType = argType;
            }
            // Boolean -> boolean
            String methodName = preparedStatementMethodMappings.get(argType);
            Method method = PreparedStatement.class.getMethod(methodName, int.class, wrapperType);
            method.invoke(preparedStatement, i + 1, arg);
        }
        return preparedStatement;
    }

    private <T> T resultSetToPojo(ResultSet resultSet, Class<T> returnType) throws Throwable {
        BeanInfo beanInfo = Introspector.getBeanInfo(returnType, Object.class);
        Constructor<T> constructor = returnType.getConstructor();
        T returnValue = constructor.newInstance();
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            String fieldName = propertyDescriptor.getName();
            Class<?> fieldType = propertyDescriptor.getPropertyType();
            String methodName = resultSetMethodMappings.get(fieldType);
            // 可能存在映射关系（不过此处是相等的）
            String columnLabel = mapColumnLabel(fieldName);
            Method resultSetMethod = ResultSet.class.getMethod(methodName, String.class);
            // 通过放射调用 getXXX(String) 方法
            Object resultValue = resultSetMethod.invoke(resultSet, columnLabel);
            // PropertyDescriptor ReadMethod 等于 Getter 方法
            // PropertyDescriptor WriteMethod 等于 Setter 方法
            Method setterMethodFromUser = propertyDescriptor.getWriteMethod();
            // 以 id 为例，  user.setId(resultSet.getLong("id"));
            setterMethodFromUser.invoke(returnValue, resultValue);
        }
        return returnValue;
    }

    private String mapColumnLabel(String fieldName) {
        return fieldName;
    }
}
