package org.geektimes.projects.user.web.listener;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.configuration.microprofile.config.DefaultConfigProviderResolver;
import org.geektimes.context.ComponentContext;
import org.geektimes.function.ThrowableAction;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.management.UserManager;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.jms.*;
import javax.management.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

/**
 * 测试用途
 */
@Deprecated
public class TestingListener implements ServletContextListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ComponentContext context = ComponentContext.getInstance();
        DBConnectionManager dbConnectionManager = context.getComponent("bean/DBConnectionManager");
        dbConnectionManager.getConnection();
        testPropertyFromJNDI(context);

//        testConfig();

//        testUser(dbConnectionManager.getEntityManager());
        logger.info("所有的 JNDI 组件名称：[");
        context.getComponentNames().forEach(logger::info);
        logger.info("]");

//        testMbean();


//        ConnectionFactory connectionFactory = context.getComponent("jms/activemq-factory");
//        testJms(connectionFactory);

    }


    private void testJms(ConnectionFactory connectionFactory) {
        ThrowableAction.execute(() -> {
//            testMessageProducer(connectionFactory);
            testMessageConsumer(connectionFactory);
        });
    }


    private void testMessageProducer(ConnectionFactory connectionFactory) throws JMSException {
        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Create a messages
        String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
        TextMessage message = session.createTextMessage(text);

        // Tell the producer to send the message
        producer.send(message);
        System.out.printf("[Thread : %s] Sent message : %s\n", Thread.currentThread().getName(), message.getText());

        // Clean up
        session.close();
        connection.close();

    }

    private void testMessageConsumer(ConnectionFactory connectionFactory) throws JMSException {

        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageConsumer from the Session to the Topic or Queue
        MessageConsumer consumer = session.createConsumer(destination);

        consumer.setMessageListener(m -> {
            TextMessage tm = (TextMessage) m;
            try {
                System.out.printf("[Thread : %s] Received : %s\n", Thread.currentThread().getName(), tm.getText());
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });

        // Clean up
        // session.close();
        // connection.close();
    }


    private void testMbean() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        // 为 UserMXBean 定义 ObjectName
        ObjectName objectName = null;
        try {
            objectName = new ObjectName("org.geektimes.projects.user.management:type=User");
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
//        ObjectName objectName = new ObjectName("aaa:bbb=ccc");
        // 创建 UserMBean 实例
        User user = new User();
        user.setEmail("adfasdf@qq.com");
        try {
            mBeanServer.registerMBean(new UserManager(user), objectName);
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    private void testConfig() {
        ConfigProviderResolver configProviderResolver = DefaultConfigProviderResolver.instance();
        logger.info(configProviderResolver.toString());
        ConfigBuilder configBuilder = configProviderResolver.getBuilder();

        //添加默认配置源
        configBuilder.addDefaultSources();



        //添加转换器
        configBuilder.addDiscoveredConverters();//SPI

        Config config = configBuilder.build();

        String value = config.getValue("application.name", String.class);
        String message = config.getValue("message", String.class);
        logger.info("application.name："+value);
        logger.info("所有的属性："+config.getPropertyNames());
    }

    /**
     * 从JNDI读取配置
     * @param context
     */
    private void testPropertyFromJNDI(ComponentContext context) {
        String propertyName = "maxValue";
        logger.info("JNDI Property[" + propertyName + "] : "
                + context.lookupComponent(propertyName));
    }

    private void testUser(EntityManager entityManager) {
        User user = new User();
        user.setName("小马哥");
        user.setPassword("******");
        user.setEmail("mercyblitz@gmail.com");
        user.setPhoneNumber("abcdefg");
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        transaction.commit();
        System.out.println(entityManager.find(User.class, user.getId()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
