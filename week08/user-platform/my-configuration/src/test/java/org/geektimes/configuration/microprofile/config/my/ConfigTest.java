package org.geektimes.configuration.microprofile.config.my;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.geektimes.configuration.microprofile.config.DefaultConfigProviderResolver;

/**
 * @Auther: liuyj
 * @Date: 2021/03/15/10:26
 * @Description:
 */
public class ConfigTest {
    public static void main(String[] args) {
        ConfigProviderResolver configProviderResolver = DefaultConfigProviderResolver.instance();

        System.out.println("configProviderResolver="+configProviderResolver);

        Config config = configProviderResolver.getConfig();
        System.out.println("config="+config);

        Iterable<ConfigSource> configSources = config.getConfigSources();
        configSources.forEach(System.out::println);

        System.out.println(configProviderResolver.getConfig().getConverter(Integer.class).get().convert("123"));

        configProviderResolver.getConfig().getPropertyNames().forEach(System.out::println);

    }
}
