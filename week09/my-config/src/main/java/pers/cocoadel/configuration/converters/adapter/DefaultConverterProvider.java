package pers.cocoadel.configuration.converters.adapter;

import org.apache.commons.configuration.PropertyConverter;
import org.eclipse.microprofile.config.spi.Converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class DefaultConverterProvider implements ConverterProvider{

    public List<Converter<?>> getConverters() {
        List<Converter<?>> list = new LinkedList<>();
        list.add(new ConverterAdapter<>(BigInteger.class, PropertyConverter::toBigInteger));
        list.add(new ConverterAdapter<>(BigDecimal.class, PropertyConverter::toBigDecimal));
        list.add(new ConverterAdapter<>(Boolean.class, PropertyConverter::toBoolean));
        list.add(new ConverterAdapter<>(Byte.class, PropertyConverter::toByte));
        list.add(new ConverterAdapter<>(Double.class, PropertyConverter::toDouble));
        list.add(new ConverterAdapter<>(Float.class, PropertyConverter::toFloat));
        list.add(new ConverterAdapter<>(Integer.class, PropertyConverter::toInteger));
        list.add(new ConverterAdapter<>(Long.class, PropertyConverter::toLong));
        list.add(new ConverterAdapter<>(Short.class, PropertyConverter::toShort));
        list.add(new ConverterAdapter<>(Character.class, PropertyConverter::toCharacter));
        return list;
    }
}
