package pers.cocoadel.configuration.sources.servlet;

import org.eclipse.microprofile.config.Config;

public class ConfigRequestContext extends ThreadLocal<Config> {
    public final static ConfigRequestContext INSTANCE = new ConfigRequestContext();

    private ConfigRequestContext(){

    }
}
