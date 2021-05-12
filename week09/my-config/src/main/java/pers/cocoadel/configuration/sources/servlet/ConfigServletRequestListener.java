package pers.cocoadel.configuration.sources.servlet;


import org.eclipse.microprofile.config.ConfigProvider;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class ConfigServletRequestListener implements ServletRequestListener {

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        ConfigRequestContext.INSTANCE.remove();
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ConfigRequestContext.INSTANCE.set(ConfigProvider.getConfig());
    }
}
