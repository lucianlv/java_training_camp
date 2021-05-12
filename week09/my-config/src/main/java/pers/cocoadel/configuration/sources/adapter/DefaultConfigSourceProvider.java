package pers.cocoadel.configuration.sources.adapter;

import org.apache.commons.configuration.*;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link ConfigSourceProvider} 实现
 * 返回 基于 Apache commons Config 实现的 ConfigSource 对象
 */
public class DefaultConfigSourceProvider implements ConfigSourceProvider {

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
        if (forClassLoader == null) {
            forClassLoader = Thread.currentThread().getContextClassLoader();
        }

        //Properties
        PropertiesConfigSourceProvider propertiesConfigSourceProvider = new PropertiesConfigSourceProvider();
        List<ConfigSourceAdapter> providerConfigSources = propertiesConfigSourceProvider.getConfigSources(forClassLoader);
        Set<ConfigSource> configSources = new HashSet<>(providerConfigSources);

        //System properties
        configSources.add(new ConfigSourceAdapter(new SystemConfiguration(), "System ConfigSource", 200));
        //Environment properties
        configSources.add(new ConfigSourceAdapter(new EnvironmentConfiguration(), "Environment ConfigSource", 100));
        addConfigurationListeners(configSources, forClassLoader);
        return configSources;
    }

    private void addConfigurationListeners(Iterable<ConfigSource> configSources,ClassLoader forClassLoader){
        ServiceLoader<ConfigurationListener> serviceLoader = ServiceLoader.load(ConfigurationListener.class, forClassLoader);
        Iterator<ConfigurationListener> iterator = serviceLoader.iterator();
        List<ConfigurationListener> listeners = new LinkedList<>();
        while (iterator.hasNext()) {
            listeners.add(iterator.next());
        }
        if (listeners.size() > 0) {
            for (ConfigSource configSource : configSources) {
                ConfigSourceAdapter configSourceAdapter = (ConfigSourceAdapter) configSource;
                configSourceAdapter.addConfigurationListener(listeners);
            }
        }
    }

    /**
     * 为每个 ClassPath 下的 META-INF/microprofile-config.properties 文件 创建一个 ConfigSourceAdapter
     */
    private static class PropertiesConfigSourceProvider {

        private static final int priority = 300;

        private static final String propertiesFileName = "META-INF/microprofile-config.properties";

        private static final Logger logger = Logger.getLogger(PropertiesConfigSourceProvider.class.getName());

        private static Collection<URL> resolvePropertyFiles(ClassLoader forClassLoader) throws IOException {
            // de-duplicate
            Map<String, URL> propertyFileUrls = resolveUrls(propertiesFileName, forClassLoader);
            // and once again with preceding a "/"
            propertyFileUrls.putAll(resolveUrls("/" + propertiesFileName, forClassLoader));
            return propertyFileUrls.values();
        }

        private static Map<String, URL> resolveUrls(String propertyFileName, ClassLoader forClassLoader) throws IOException {
            Map<String, URL> propertyFileUrls = new HashMap<>();
            Enumeration<URL> urls = forClassLoader.getResources(propertyFileName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                propertyFileUrls.put(url.toExternalForm(), url);
            }
            return propertyFileUrls;
        }

        public List<ConfigSourceAdapter> getConfigSources(ClassLoader forClassLoader) {
            List<ConfigSourceAdapter> sources = new LinkedList<>();
            try {
                Collection<URL> urls = resolvePropertyFiles(forClassLoader);
                for (URL url : urls) {
                    PropertiesConfiguration configuration = new PropertiesConfiguration(url);
                    String name = "propertiesConfigSource - " + url.toString();
                    ConfigSourceAdapter configSource = new ConfigSourceAdapter(configuration, name, priority);
                    sources.add(configSource);
                }
            } catch (IOException | ConfigurationException e) {
                logger.log(Level.WARNING, String.format("load file %s failure", propertiesFileName));
            }
            return sources;
        }
    }
}
