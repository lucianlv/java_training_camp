package pers.cocoadel.configuration.sources.adapter;

import org.apache.commons.configuration.*;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.eclipse.microprofile.config.spi.ConfigSource;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * microprofile {@link ConfigSource} 接口的通用实现，主要是适配 apache commons {@link Configuration}
 */
public class ConfigSourceAdapter implements ConfigSource{

    private static final Logger logger = Logger.getLogger(ConfigSourceAdapter.class.getName());

    /**
     * apache commons {@link Configuration} 对象
     */
    private final CompositeConfiguration configuration;

    /**
     * 名称
     */
    private final String name;

    /**
     * 配置源的优先级
     */
    private int ordinal = 100;

    public ConfigSourceAdapter(Configuration configuration, String name, int defaultOrdinal) {
        this.configuration = new CompositeConfiguration(configuration);
        this.name = name;
        initOrdinal(defaultOrdinal);
    }

    @Override
    public Set<String> getPropertyNames() {
        Set<String> set = new HashSet<>();
        Iterator<String> iterator = configuration.getKeys();
        while (iterator.hasNext()) {
            set.add(iterator.next());
        }
        return set;
    }

    @Override
    public String getValue(String propertyName) {
        return configuration.getString(propertyName);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    protected void initOrdinal(int defaultOrdinal) {
        ordinal = defaultOrdinal;

        String configuredOrdinalString = getValue(CONFIG_ORDINAL);
        try {
            if (configuredOrdinalString != null) {
                ordinal = Integer.parseInt(configuredOrdinalString.trim());
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,
                    "The configured config-ordinal isn't a valid integer. Invalid value: " + configuredOrdinalString);
        }
    }


    public void setProperty(String propertyName, String value) {
        configuration.setProperty(propertyName,value);
//        configuration.clearProperty(propertyName);
//        configuration.setProperty(propertyName, value);
//        if (configuration instanceof PropertiesConfiguration) {
//            PropertiesConfiguration propertiesConfiguration = (PropertiesConfiguration) configuration;
//            try {
//                propertiesConfiguration.save();
//            } catch (ConfigurationException e) {
//                String error = "save propertyName %s value %s to propertiesConfiguration failure! : %s";
//                logger.log(Level.WARNING,
//                        String.format(error,propertyName,value,ExceptionUtils.getFullStackTrace(e)));
//            }
//        }
    }

    protected void addConfigurationListener(Collection<ConfigurationListener> listeners) {
//        if (configuration instanceof AbstractConfiguration) {
//            AbstractConfiguration abstractConfiguration = (AbstractConfiguration) configuration;
//
//        }
        listeners.forEach(configuration::addConfigurationListener);
    }


}
