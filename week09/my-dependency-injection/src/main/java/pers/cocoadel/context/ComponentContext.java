package pers.cocoadel.context;

import javafx.util.Pair;
import org.apache.commons.lang.exception.ExceptionUtils;
import pers.cocoadel.context.function.ThrowableFunction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.naming.*;
import javax.servlet.ServletContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ComponentContext {

    public final static String CONTEXT_NAME = ComponentContext.class.getName();

    private final static String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

    private static final Logger logger = Logger.getLogger(CONTEXT_NAME);

    //这里不需要考虑线程安全问题，因为这里是由 ServletListener contextInitialized 进行加载，这是一个单线程的过程不需要考虑线程安全问题
    private final Map<String, Object> componentMap = new HashMap<>();

    private final Map<String, ComponentInfo> componentInfoMap = new HashMap<>();

    private Context envContext;

    private static ServletContext servletContext;

    // 假设一个 Tomcat JVM 进程，三个 Web Apps，会不会相互冲突？（不会冲突）
    // static 字段是 JVM 缓存吗？（是 ClassLoader 缓存）
    private ClassLoader classLoader;

    private final LinkedHashSet<Class<? extends Annotation>> injectAnnotationSet = new LinkedHashSet<>();

    public static ComponentContext getInstance() {
        return (ComponentContext) servletContext.getAttribute(CONTEXT_NAME);
    }

    public void init(ServletContext servletContext) {
        ComponentContext.servletContext = servletContext;
        ComponentContext.servletContext.setAttribute(CONTEXT_NAME, this);
        this.classLoader = servletContext.getClassLoader();
        injectAnnotationSet.add(Resource.class);
        initEnvContext();
        initComponents();
        initializeComponentInfo();
        initializedComponents();
    }

    public void close() {
        processPreDestroy();
    }


    private void initEnvContext() {
        try {
            Context context = new InitialContext();
            envContext = (Context) context.lookup(COMPONENT_ENV_CONTEXT_NAME);
        } catch (NamingException e) {
            logger.log(Level.SEVERE, CONTEXT_NAME + " init failure: " + ExceptionUtils.getFullStackTrace(e));
        }
    }


    public <T> T getComponent(String name, Class<T> type) {
        Object obj = componentMap.get(name);
        return obj == null ? null : type.cast(obj);
    }

    protected <T> T lookupComponent(String name, Class<T> type) {
        return executeInContext(context -> {
            Object obj = context.lookup(name);
            return obj == null ? null : type.cast(obj);
        });
    }

    private void initComponents() {
        List<String> componentNames = findAllComponentNames();
        for (String name : componentNames) {
            Object bean = lookupComponent(name, Object.class);
            if (bean != null) {
                componentMap.put(name, bean);
            }
        }
    }

    private List<String> findAllComponentNames() {
        return findComponentNames("/");
    }

    private List<String> findComponentNames(final String root) {
        return executeInContext((Context) -> {
            List<String> result = new LinkedList<>();
            NamingEnumeration<NameClassPair> elements = envContext.list(root);
            //如果 root 目录下没有字节的，则直接返回
            if (elements == null) {
                return result;
            }
            while (elements.hasMoreElements()) {
                NameClassPair pair = elements.nextElement();
                Class<?> clazz = classLoader.loadClass(pair.getClassName());
                //目录会返回 Context 对象
                if (Context.class.isAssignableFrom(clazz)) {
                    result.addAll(findComponentNames(pair.getName()));
                } else {
                    String beanName = root.startsWith("/") ?
                            pair.getName() : root + "/" + pair.getName();
                    result.add(beanName);
                }
            }
            return result;
        });
    }

    protected void initializeComponentInfo() {
        for (String componentName : componentMap.keySet()) {
            Object component = componentMap.get(componentName);
            Class<?> clazz = component.getClass();
            ComponentInfo componentInfo = new ComponentInfo();
            componentInfo.setComponentName(componentName);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    componentInfo.addPostConstructMethod(method);
                }

                if (method.isAnnotationPresent(PreDestroy.class)) {
                    componentInfo.addPreDestroyMethods(method);
                }
            }
            componentInfoMap.put(componentName, componentInfo);
        }
    }


    //自动依赖注入
    //初始化
    //消耗回调
    private void initializedComponents() {
        processInjections();
        processPostConstruct();
    }

    private void processInjections() {
        Collection<Object> components = componentMap.values();
        for (Object component : components) {
            injectComponent(component, component.getClass());
        }
    }

    public void injectComponent(Object component, Class<?> clazz) {
        Stream.of(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(field -> {
                    Annotation annotation = matchInjectAnnotation(field);
                    return new Pair<>(field, annotation);
                })
                .filter(pair -> pair.getValue() != null)
                .forEach(pair -> {
                    Field field = pair.getKey();
                    Annotation annotation = pair.getValue();
                    String injectName = getInjectNameFromAnnotation(annotation);
                    Object injectBean = getComponent(injectName, Object.class);
                    if (injectBean != null) {
                        field.setAccessible(true);
                        try {
                            field.set(component, injectBean);
                        } catch (IllegalAccessException e) {
                            logger.log(Level.SEVERE, ExceptionUtils.getFullStackTrace(e));
                        }
                    }
                });
    }

    private Annotation matchInjectAnnotation(Field field) {
        return injectAnnotationSet
                .stream()
                .filter(field::isAnnotationPresent)
                .findFirst()
                .map(field::getAnnotation)
                .orElse(null);
    }

    private String getInjectNameFromAnnotation(Annotation annotation) {
        if (annotation != null) {
            if (annotation instanceof Resource) {
                Resource resource = (Resource) annotation;
                return resource.name();
            }
        }
        return "";
    }

    private void processPostConstruct() {
        componentInfoMap.values().forEach(componentInfo -> {
            Set<Method> postConstructMethods = componentInfo.getPostConstructMethods();
            postConstructMethods.forEach(method -> {
                try {
                    method.invoke(getComponent(componentInfo.getComponentName(), Object.class));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.log(Level.SEVERE, ExceptionUtils.getFullStackTrace(e));
                }
            });
        });
    }


    private void processPreDestroy() {
        componentInfoMap.values().forEach(componentInfo -> {
            Set<Method> postConstructMethods = componentInfo.getPreDestroyMethods();
            postConstructMethods.forEach(method -> {
                try {
                    method.invoke(getComponent(componentInfo.getComponentName(), Object.class));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.log(Level.SEVERE, ExceptionUtils.getFullStackTrace(e));
                }
            });
        });
    }

    private <R> R executeInContext(ThrowableFunction<Context, R> function) {
        return executeInContext(envContext, function);
    }

    private <R> R executeInContext(final Context context, ThrowableFunction<Context, R> function) {
        return executeInContext(context, function, false);
    }

    private <R> R executeInContext(final Context context, ThrowableFunction<Context, R> function, boolean ignoredException) {
        try {
            return function.execute(context);
        } catch (Throwable throwable) {
            if (ignoredException) {
                logger.log(Level.SEVERE, ExceptionUtils.getFullStackTrace(throwable));
            } else {
                throw new RuntimeException(throwable);
            }
        }
        return null;
    }
}
