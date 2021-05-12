package pers.cocoadel.configuration.impl;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import java.util.HashMap;
import java.util.Map;

public class ConfigProviderResolverImpl extends ConfigProviderResolver {

    private final Map<ClassLoader, Config> configMap = new HashMap<>();

    @Override
    public Config getConfig() {
        return getConfig(null);
    }

    @Override
    public Config getConfig(ClassLoader classLoader) {
        if (!configMap.containsKey(classLoader)) {
            Config config = getBuilder().addDefaultSources()
                    .addDiscoveredSources()
                    .addDiscoveredConverters()
                    .build();
            configMap.put(classLoader, config);
        }
        return configMap.get(classLoader);
    }

    @Override
    public ConfigBuilder getBuilder() {
        return new ConfigBuilderImpl();
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {
        if (configMap.containsKey(classLoader)) {
            throw new IllegalStateException(
                    String.format("the config of classLoader %s already exist! you must release it first", classLoader));
        }
        configMap.put(classLoader, config);
    }

    @Override
    public void releaseConfig(Config config) {
        configMap.remove(config.getClass().getClassLoader());
    }
}
