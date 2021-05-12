package pres.cocoadel.web.mvc;

import java.lang.reflect.Method;
import java.util.Set;

public class HandlerMethodInfo {

    private String requestMappingPath;

    private Method handlerMethod;

    private Set<String> supportedHttpMethods;

    public HandlerMethodInfo(String requestMappingPath, Method handlerMethod,
                             Set<String> supportedHttpMethods) {
        this.requestMappingPath = requestMappingPath;
        this.handlerMethod = handlerMethod;
        this.supportedHttpMethods = supportedHttpMethods;
    }

    public String getRequestMappingPath() {
        return requestMappingPath;
    }

    public void setRequestMappingPath(String requestMappingPath) {
        this.requestMappingPath = requestMappingPath;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public void setHandlerMethod(Method handlerMethod) {
        this.handlerMethod = handlerMethod;
    }

    public Set<String> getSupportedHttpMethods() {
        return supportedHttpMethods;
    }

    public void setSupportedHttpMethods(Set<String> supportedHttpMethods) {
        this.supportedHttpMethods = supportedHttpMethods;
    }
}
