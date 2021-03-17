package org.geektimes.configuration.microprofile.config.converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.microprofile.config.spi.Converter;

public class BooleanConverter implements Converter <Boolean>{

    private final Set<String> values = new HashSet<>(Arrays.asList("true", "1", "YES", "Y", "ON"));

    @Override
    public Boolean convert(String value) throws IllegalArgumentException, NullPointerException {
        return values.contains(value);
    }
}
