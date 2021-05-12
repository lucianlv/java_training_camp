package pers.cocoadel.configuration.impl;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import pers.cocoadel.configuration.converters.adapter.ConverterTypeUtils;

import javax.annotation.Priority;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;

/**
 * 实现 {@link org.eclipse.microprofile.config.Config}
 */
public class ConfigImpl implements Config {
    private final static int CONVERTER_DEFAULT_PRIORITY = 100;

    private final List<ConfigSource> configSources = new LinkedList<>();

    private final Map<Type, Converter> converterMap = new HashMap<>();

    @Override
    public <T> T getValue(String s, Class<T> aClass) {
        String value = getPropertyValue(s);
        if (StringUtils.isBlank(value) || aClass.equals(String.class)) {
            return aClass.cast(value);
        }
        Converter<T> converter = getConverter(aClass).orElse(null);
        if (converter == null) {
            throw new IllegalStateException("no support type convert: " + aClass.getName());
        }
        return converter.convert(value);
    }

    protected String getPropertyValue(String propertyName) {
        String propertyValue = null;
        for (ConfigSource configSource : configSources) {
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null) {
                break;
            }
        }
        return propertyValue;
    }

    @Override
    public ConfigValue getConfigValue(String s) {
        return null;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String s, Class<T> aClass) {
        T value = getValue(s, aClass);
        return Optional.ofNullable(value);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (ConfigSource configSource : configSources) {
            set.addAll(configSource.getPropertyNames());
        }
        return new ArrayList<>(set);
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return Collections.unmodifiableList(configSources);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> aClass) {
        Converter<T> converter = (Converter<T>) converterMap.get(aClass);
        return Optional.ofNullable(converter);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }

    protected synchronized void addConfigSources(Collection<ConfigSource> configSources) {
        Set<ConfigSource> set = new HashSet<>(this.configSources);
        set.addAll(configSources);
        this.configSources.clear();
        this.configSources.addAll(set);
        this.configSources.sort((o1, o2) -> o2.getOrdinal() - o1.getOrdinal());
    }

    protected synchronized void addConverters(Collection<Converter<?>> converters) {
        for (Converter<?> converter : converters) {
            Type type = ConverterTypeUtils.getTypeOfConverter(converter);
            Converter<?> oldConverter = converterMap.get(type);
            Converter<?> newConverter;
            if (oldConverter == null) {
                newConverter = converter;
            } else {
                newConverter = getConvertPriority(converter) > getConvertPriority(oldConverter) ?
                        converter : oldConverter;
            }
            converterMap.put(type, newConverter);
        }
    }

    private int getConvertPriority(Converter<?> converter) {
        int priority = CONVERTER_DEFAULT_PRIORITY;
        Class<?> clazz = converter.getClass();
        if (clazz.isAnnotationPresent(Priority.class)) {
            priority = clazz.getAnnotation(Priority.class).value();
        }
        return priority;
    }
}
