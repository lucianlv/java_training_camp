package org.geektimes.configuration.microprofile.config.source;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.microprofile.config.spi.ConfigSource;

public class JavaSystemEnvironmentConfigSource implements ConfigSource {

    private final Map<String, String> properties;

    public JavaSystemEnvironmentConfigSource() {
        this.properties = new HashMap<>();
        System.getenv().forEach((k, v) -> properties.put(k, v));
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public int getOrdinal() {
        return 300;
    }

    @Override
    public String getValue(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public String getName() {
        return "os environment variables";
    }
}
