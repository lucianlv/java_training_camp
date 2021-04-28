package org.geektimes.configuration.microprofile.config.my;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.geektimes.configuration.microprofile.config.DefaultConfigProviderResolver;
import org.geektimes.configuration.microprofile.config.converter.Converters;
import org.geektimes.configuration.microprofile.config.converter.DoubleConverter;
import org.geektimes.configuration.microprofile.config.converter.IntegerConverter;

import java.util.Optional;

public class ConfigBuilderTest {
    public static void main(String[] args) {
        ConfigProviderResolver configProviderResolver = DefaultConfigProviderResolver.instance();
        System.out.println("configProviderResolver="+configProviderResolver);

        ConfigBuilder configBuilder = configProviderResolver.getBuilder();

        //添加默认配置源
//        configBuilder.addDefaultSources();
        configBuilder.addDefaultSources();

        //添加转换器
        configBuilder.addDiscoveredConverters();//SPI

        configBuilder.withConverter(Integer.class, Converters.DEFAULT_PRIORITY,new IntegerConverter());//java api
        configBuilder.withConverter(Double.class,Converters.DEFAULT_PRIORITY,new DoubleConverter());//java api

        Config config = configBuilder.build();

        config.getConfigSources().forEach(configSource -> {
            System.out.println(configSource.getValue("application.name"));
        });

        Iterable<ConfigSource> configSources = config.getConfigSources();
        configSources.forEach(System.out::println);

        Optional<Converter<Integer>> converter = config.getConverter(Integer.class);
        System.out.println(converter);

        System.out.println(converter.get().convert("123"));

    }
}
