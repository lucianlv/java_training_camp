package pers.cocoadel.configuration.converters.adapter;

import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ConverterTypeUtils {

    /**
     * 获取 Converter<T>  T的实际类型
     * @param converter 对象
     * @return T 的实际类型
     */
    public static Type getTypeOfConverter(Converter<?> converter) {
        //如果 converter 是 ConverterAdapter 对象,则直接通过方法 converterAdapter#getType 获取
        if (isConverterAdapter(converter.getClass())) {
            ConverterAdapter<?> converterAdapter = (ConverterAdapter<?>) converter;
            return converterAdapter.getType();
        }
        //如果是 Converter 的直接实现，则通过 ParameterizedType 获取
        return getTypeOfConverter(converter.getClass());
    }

    private static boolean isConverterAdapter(Class<?> clazz) {
        return clazz.isAssignableFrom(ConverterAdapter.class);
    }

    private static Type getTypeOfConverter(Class<?> clazz) {
        if (Object.class.equals(clazz)) {
            return null;
        }
        Type[] types = clazz.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType().equals(Converter.class)) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length != 1) {
                        String error = String.format("converter class %s must be a ParameterizedType class!",
                                clazz.getName());
                        throw new IllegalStateException(error);
                    }
                    return actualTypeArguments[0];
                }
            }
        }
        return getTypeOfConverter(clazz.getSuperclass());
    }
}
