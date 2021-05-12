package pers.cocoadel.configuration.converters.adapter;

import org.eclipse.microprofile.config.spi.Converter;

import java.util.List;

public interface ConverterProvider {

    List<Converter<?>> getConverters();
}
