package pers.cocoadel.configuration.converters;

public class LongConverter extends AbstractConverter<Long> {

    @Override
    protected Long doConvert(String value) {
        return Long.valueOf(value);
    }
}
