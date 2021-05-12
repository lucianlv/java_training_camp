package pers.cocoadel.configuration.impl;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.eclipse.microprofile.config.spi.Converter;
import pers.cocoadel.configuration.converters.adapter.ConverterProvider;

import java.util.*;


public class ConfigBuilderImpl implements ConfigBuilder {

    private final Set<ConfigSource> configSources = new HashSet<>();

    private final Set<Converter<?>> converters = new HashSet<>();

    private final Map<Class, PrioritisedConverter> prioritisedConverters = new HashMap<>();

    private boolean addDefaultSources = false;

    private boolean addDiscoveredSources = false;

    private boolean addDiscoveredConverters = false;

    private ClassLoader forClassLoader;

    @Override
    public ConfigBuilder addDefaultSources() {
        addDefaultSources = true;
        return this;
    }

    @Override
    public ConfigBuilder addDiscoveredSources() {
        addDiscoveredSources = true;
        return this;
    }

    @Override
    public ConfigBuilder addDiscoveredConverters() {
        addDiscoveredConverters = true;
        return this;
    }

    @Override
    public ConfigBuilder forClassLoader(ClassLoader loader) {
        this.forClassLoader = loader;
        return null;
    }

    @Override
    public ConfigBuilder withSources(ConfigSource... sources) {
        configSources.addAll(Arrays.asList(sources));
        return this;
    }

    @Override
    public ConfigBuilder withConverters(Converter<?>... converters) {
        this.converters.addAll(Arrays.asList(converters));
        return this;
    }

    @Override
    public <T> ConfigBuilder withConverter(Class<T> type, int priority, Converter<T> converter) {
        PrioritisedConverter prioritisedConverter = prioritisedConverters.get(type);
        if (prioritisedConverter == null) {
            prioritisedConverter = new PrioritisedConverter(type, converter, priority);
        } else {
            if (prioritisedConverter.priority == priority) {
                throw new IllegalStateException(String
                        .format("the type %s converter with the same priority %s  already exists!", type, priority));
            }
            if (priority > prioritisedConverter.priority) {
                prioritisedConverter = new PrioritisedConverter(type, converter, priority);
            }
        }
        prioritisedConverters.put(type, prioritisedConverter);
        return this;
    }

    @Override
    public Config build() {
        ConfigImpl config = new ConfigImpl();
        loadConfigSources();
        config.addConfigSources(configSources);

        loadConverters();
        config.addConverters(converters);
        return config;
    }

    /**
     * 通过 SPI 加载 ConfigSource 实现
     */
    protected void loadConfigSources() {
        if (addDefaultSources) {
            this.configSources.addAll(getDefaultSources());
        }
        if (addDiscoveredSources) {
            ClassLoader classLoader = getForClassLoader();
            //通过 SPI 加载 ConfigSource
            ServiceLoader<ConfigSource> configSourceServiceLoader = ServiceLoader.load(ConfigSource.class, classLoader);
            for (ConfigSource configSource : configSourceServiceLoader) {
                this.configSources.add(configSource);
            }

            //通过 SPI 加载 ConfigSourceProvider，通过 Provider 获取需要注册的 ConfigSource
            ServiceLoader<ConfigSourceProvider> configSourceProviders =
                    ServiceLoader.load(ConfigSourceProvider.class, classLoader);
            for (ConfigSourceProvider provider : configSourceProviders) {
                Iterable<ConfigSource> configSources = provider.getConfigSources(classLoader);
                for (ConfigSource source : configSources) {
                    this.configSources.add(source);
                }
            }
        }
    }

    /**
     * 通过 SPI 加载 Converter 的实现
     */
    protected void loadConverters() {
        if (addDiscoveredConverters) {
            //通过 SPI 加载 Converter 实现
            ClassLoader classLoader = getForClassLoader();
            ServiceLoader<Converter> converterServiceLoader = ServiceLoader.load(Converter.class, classLoader);
            for (Converter converter : converterServiceLoader) {
                this.converters.add(converter);
            }

            //通过 spi 加载 ConverterProvider，再通过 ConverterProvider 加载 Converter
            ServiceLoader<ConverterProvider> providerServiceLoader =
                    ServiceLoader.load(ConverterProvider.class, classLoader);
            for (ConverterProvider converterProvider : providerServiceLoader) {
                this.converters.addAll(converterProvider.getConverters());
            }
        }
        prioritisedConverters
                .values()
                .forEach(prioritisedConverter -> converters.add(prioritisedConverter.converter));
    }

    protected ClassLoader getForClassLoader() {
        if (this.forClassLoader == null) {
            forClassLoader = Thread.currentThread().getContextClassLoader();
        }
        return forClassLoader;
    }

    protected List<ConfigSource> getDefaultSources() {
        return Collections.emptyList();
    }


    static class PrioritisedConverter {
        private final int priority;
        private final Class<?> type;
        private final Converter<?> converter;

        PrioritisedConverter(Class<?> type, Converter<?> converter, int priority) {
            this.priority = priority;
            this.converter = converter;
            this.type = type;
        }
    }
}
