package pres.cocoadel.web.mvc;

import org.apache.commons.lang.StringUtils;
import pers.cocoadel.context.ComponentContext;
import pres.cocoadel.web.mvc.controller.Controller;
import pres.cocoadel.web.mvc.controller.PageController;
import pres.cocoadel.web.mvc.controller.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class FrontControllerServlet extends HttpServlet {

    private final Map<String, HandlerMethodInfo> handlerMethodInfoMap = new HashMap<>();

    private final Map<String, Controller> controllerMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        config.getServletContext().log("FrontControllerServlet init....");
        initHandlerMethods(config);
    }

    private void initHandlerMethods(ServletConfig servletConfig) {
        ServiceLoader<Controller> controllers = ServiceLoader.load(Controller.class);
        ComponentContext componentContext = (ComponentContext) servletConfig.getServletContext()
                .getAttribute(ComponentContext.CONTEXT_NAME);
        controllers.forEach(controller -> componentContext.injectComponent(controller, controller.getClass()));
        initHandlerMethods(controllers);
    }

    private void initHandlerMethods(Iterable<Controller> controllers) {
        for (Controller controller : controllers) {
            Class<?> controllerClass = controller.getClass();
            Path pathAnnotation = controllerClass.getAnnotation(Path.class);
            String controllerPath = pathAnnotation.value();
            Method[] methods = controllerClass.getMethods();
            for (Method method : methods) {
                Path methodPathAnnotation = method.getAnnotation(Path.class);
                if (methodPathAnnotation == null) {
                    continue;
                }
                String methodPath = methodPathAnnotation.value();
                String requestMappingPath = controllerPath + methodPath;
                if (handlerMethodInfoMap.containsKey(requestMappingPath)) {
                    String error = String.format("request path %s already exist!", requestMappingPath);
                    throw new RuntimeException(error);
                }
                Set<String> supportedMethods = findSupportedHttpMethods(method);
                HandlerMethodInfo handlerMethodInfo = new HandlerMethodInfo(requestMappingPath, method, supportedMethods);
                handlerMethodInfoMap.put(requestMappingPath, handlerMethodInfo);
                controllerMap.put(requestMappingPath, controller);
            }
        }
    }

    private Set<String> findSupportedHttpMethods(Method method) {
        Set<String> set = new LinkedHashSet<>();
        HttpMethod[] annotations = method.getAnnotationsByType(HttpMethod.class);
        for (HttpMethod annotation : annotations) {
            set.add(annotation.value());
        }

        if (set.isEmpty()) {
            set.addAll(Arrays.asList(HttpMethod.GET, HttpMethod.DELETE, HttpMethod.HEAD,
                    HttpMethod.PUT, HttpMethod.OPTIONS, HttpMethod.POST));
        }
        return set;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String requestPath = req.getRequestURI();
        String contextPath = req.getContextPath();
        String prefixPath = contextPath;
        String requestMappingPath = StringUtils.substringAfter(requestPath,
                prefixPath.replaceAll("//", "/"));

        Controller controller = controllerMap.get(requestMappingPath);
        HandlerMethodInfo handlerMethodInfo = handlerMethodInfoMap.get(requestMappingPath);

        try {
            if (controller instanceof PageController &&
                    handlerMethodInfo.getHandlerMethod().getName().equals("execute")) {
                PageController pageController = (PageController) controller;
                doPageController(pageController, handlerMethodInfo, requestMappingPath, req, resp);
            } else if (controller instanceof RestController) {
                doResetController((RestController) controller, handlerMethodInfo, requestMappingPath, req, resp);
            }
        } catch (Throwable throwable) {
            if (throwable.getCause() instanceof IOException) {
                throw (IOException) throwable.getCause();
            } else {
                throw new ServletException(throwable.getMessage());
            }
        }
    }

    private void doPageController(PageController pageController, HandlerMethodInfo handlerMethodInfo,
                                  String requestMappingPath, HttpServletRequest req, HttpServletResponse response) throws Throwable {
        //判断 HttpMethod 是否支持
        String httpMethod = req.getMethod();
        Set<String> supportedHttpMethods = handlerMethodInfo.getSupportedHttpMethods();
        if (!supportedHttpMethods.contains(httpMethod)) {
            // HTTP 方法不支持
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        //通过反射调用 PageController 的 execute 方法
        String viewPath = pageController.execute(req, response);
        if (!viewPath.startsWith("/")) {
            viewPath = "/" + viewPath;
        }
        // response 已经结束
        if (response.isCommitted()) {
            return;
        }
        //通过RequestDispatcher forward request
        RequestDispatcher requestDispatcher = req.getServletContext().getRequestDispatcher(viewPath);
        requestDispatcher.forward(req, response);
    }

    private void doResetController(RestController restController, HandlerMethodInfo handlerMethodInfo,
                                   String requestMappingPath, HttpServletRequest req, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException, IOException {
        //判断 HttpMethod 是否支持
        String httpMethod = req.getMethod();
        Set<String> supportedHttpMethods = handlerMethodInfo.getSupportedHttpMethods();
        if (!supportedHttpMethods.contains(httpMethod)) {
            // HTTP 方法不支持
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        //通过 反射调用 Rest 方法
        Method method = handlerMethodInfo.getHandlerMethod();
        Object returnValue = method.invoke(restController, req, response);
        //暂时先当作 String 处理 返回
        byte[] bytes = returnValue.toString().getBytes();
        response.getOutputStream().write(bytes);
    }


}
