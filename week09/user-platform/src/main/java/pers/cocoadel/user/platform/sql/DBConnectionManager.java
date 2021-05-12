package pers.cocoadel.user.platform.sql;

import org.apache.commons.lang.exception.ExceptionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnectionManager {

    private static final Logger logger = Logger.getLogger(JdbcHelper.class.getName());

    @Resource(name = "jdbc/UserPlatformDB")
    private DataSource dataSource;

    private Connection connection;

    public static final String DROP_USERS_TABLE_DDL_SQL = "DROP TABLE users";

    public static final String CREATE_USERS_TABLE_DDL_SQL = "CREATE TABLE users(" +
//            "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
            "id INT NOT NULL PRIMARY KEY, " +
            "name VARCHAR(32) NOT NULL, " +
            "password VARCHAR(64) NOT NULL, " +
            "email VARCHAR(64) NOT NULL, " +
            "phoneNumber VARCHAR(64) NOT NULL" +
            ")";

    public static final String INSERT_USER_DML_SQL = "INSERT INTO users(name,password,email,phoneNumber) VALUES " +
            "('A','******','a@gmail.com','1') , " +
            "('B','******','b@gmail.com','2') , " +
            "('C','******','c@gmail.com','3') , " +
            "('D','******','d@gmail.com','4') , " +
            "('E','******','e@gmail.com','5')";

    @PostConstruct
    public void init(){
        try {
            Statement statement = getConnection().createStatement();
            try {
                // 删除 users 表
                statement.execute(DROP_USERS_TABLE_DDL_SQL);
            } catch (Throwable throwable) {
                logger.log(Level.SEVERE, throwable.getMessage());
            }
            // 创建 users 表
            statement.execute(CREATE_USERS_TABLE_DDL_SQL);
//            System.out.println(statement.executeUpdate(INSERT_USER_DML_SQL));  // 5
        } catch (Throwable throwable) {
            logger.log(Level.SEVERE, throwable.getMessage());
        }
    }

    @PreDestroy
    public void close() {
        releaseConnection();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = dataSource.getConnection();
            } catch (SQLException throwable) {
                throw new RuntimeException(throwable);
            }
            if (connection != null) {
                logger.log(Level.INFO, "获取 JNDI 数据库连接成功！");
            }
        }
        return this.connection;
    }

    public void releaseConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
                logger.log(Level.INFO, "释放数据库连接成功！");
            } catch (SQLException e) {
                logger.log(Level.INFO, "释放数据库连接失败：" + ExceptionUtils.getFullStackTrace(e));
            }
        }
    }
}
