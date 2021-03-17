package org.geektimes.configuration.microprofile.config.source;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.microprofile.config.spi.ConfigSource;

public class CustomFileConfigSource implements ConfigSource {

    private final Map<String, String> properties;

    public CustomFileConfigSource() {
        this.properties = new HashMap<>();
        try {
            Properties properties = new Properties();
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("META-INF/custom.properties");
            properties.load(in);
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);
                this.properties.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public int getOrdinal() {
        return 200;
    }

    @Override
    public String getValue(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public String getName() {
        return "custom file Properties";
    }
}
