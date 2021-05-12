package pers.cocoadel.configuration.converters;

public class BooleanConverter extends AbstractConverter<Boolean> {

    @Override
    protected Boolean doConvert(String value) {
        return Boolean.parseBoolean(value);
    }
}
