package pers.cocoadel.user.platform.web.listener;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import pers.cocoadel.context.ComponentContext;
import pers.cocoadel.user.platform.management.ApplicationManager;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.lang.management.ManagementFactory;

/**
 * 初始化一些单例对象
 */
@WebListener
public class DBConnectionInitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().log("DBConnectionInitializerListener contextInitialized .......");
//        ComponentContext context = new ComponentContext();
//        context.init(servletContextEvent.getServletContext());
//        testConfig();
        registerMBean();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().log("DBConnectionInitializerListener contextDestroyed .......");
        ComponentContext componentContext = (ComponentContext) servletContextEvent.getServletContext()
                .getAttribute(ComponentContext.CONTEXT_NAME);
        componentContext.close();
    }

    private void testConfig() {
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
}
