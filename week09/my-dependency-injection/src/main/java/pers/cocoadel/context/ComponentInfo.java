package pers.cocoadel.context;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class ComponentInfo {

    private String componentName;

    private final Set<Method> postConstructMethods = new HashSet<>();

    private final Set<Method> preDestroyMethods = new HashSet<>();

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Set<Method> getPostConstructMethods() {
        return Collections.unmodifiableSet(postConstructMethods);
    }

    public void addPostConstructMethod(Method method) {
        this.postConstructMethods.add(method);
    }

    public Set<Method> getPreDestroyMethods() {
        return Collections.unmodifiableSet(preDestroyMethods);
    }

    public void addPreDestroyMethods(Method method) {
        this.preDestroyMethods.add(method);
    }
}
