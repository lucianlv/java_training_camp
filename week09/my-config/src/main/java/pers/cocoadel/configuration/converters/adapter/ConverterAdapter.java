package pers.cocoadel.configuration.converters.adapter;

import org.apache.commons.configuration.PropertyConverter;
import org.eclipse.microprofile.config.spi.Converter;
import javax.annotation.Priority;
import java.util.function.Function;

/**
 * 一个通用的 microprofile {@link Converter} 接口的实现
 * 主要是为了适配 apache commons 的 {@link PropertyConverter}
 */
@Priority(1)
public class ConverterAdapter<T> implements Converter<T> {

    private final Class<T> type;

    private final Function<Object,Object> function;

    public ConverterAdapter(Class<T> type, Function<Object, Object> function) {
        this.type = type;
        this.function = function;
    }

    @Override
    public T convert(String value) throws IllegalArgumentException, NullPointerException {
        Object ret = function.apply(value);
        return type.cast(ret);
    }

    public Class<T> getType() {
        return type;
    }
}
