package pers.cocoadel.configuration.converters;

public class ShortConverter extends AbstractConverter<Short> {

    @Override
    protected Short doConvert(String value) {
        return Short.valueOf(value);
    }
}
