package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.Config;

public class DefaultConfigContext {
    private DefaultConfigContext() {}

    private static final ThreadLocal<Config> configContext = new ThreadLocal<>();

    public static void set(Config config) {
        configContext.set(config);
    }

    public static Config get() {
        return configContext.get();
    }

    public static void remove() {
        configContext.remove();
    }
}
