package pers.cocoadel.user.platform.web.listener;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import pers.cocoadel.context.ComponentContext;
import pers.cocoadel.user.platform.management.ApplicationManager;
import javax.management.*;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Deprecated
public class TestListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(TestListener.class.getName());

    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().log("TestListener contextInitialized .......");
        ComponentContext context = new ComponentContext();
        context.init(servletContextEvent.getServletContext());
//        DataSource dataSource = context.getComponent("jdbc/UserPlatformDB",DataSource.class);
//        getConnection(dataSource);
//
//        DBConnectionManager connectionManager =
//                context.getComponent("bean/DBConnectionManager", DBConnectionManager.class);
//        connectionManager.init();
//        testPropertiesFromJNDI(context,servletContextEvent.getServletContext());
//        testPropertiesFromServletConfig(servletContextEvent.getServletContext());
        testConfig(context);
        registerMBean();
    }

//    private void testPropertiesFromJNDI(ComponentContext componentContext, ServletContext servletContext){
//        Integer maxValue = componentContext.getComponent("maxValue", Integer.class);
//        servletContext.log("++++++++++++++maxValue: " + maxValue);
//    }
//
//    private void testPropertiesFromServletConfig(ServletContext servletContext) {
//        String appName = servletContext.getInitParameter("application.name");
//        servletContext.log("------------application.name: " + appName);
//    }

    private void testConfig(ComponentContext componentContext) {
        Config config = ConfigProvider.getConfig();
        String applicationName = config.getValue("application.name", String.class);
        System.out.println("******************************** application.name: " + applicationName);
        Integer maxValue = config.getValue("maxValue", Integer.class);
        System.out.println("******************************** maxValue: " + maxValue);
    }

    private void registerMBean() {
        try {
            // 获取平台 MBean Server
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            // 为 UserMXBean 定义 ObjectName
            ObjectName objectName = new ObjectName("pers.cocoadel.user.platform.config.application.management:type=application");
            // 创建 UserMBean 实例
            mBeanServer.registerMBean(new ApplicationManager(), objectName);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private Connection getConnection(DataSource dataSource) {
        // 依赖查找
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        if (connection != null) {
            logger.log(Level.INFO, "获取 JNDI 数据库连接成功！");
        }
        return connection;
    }
}
