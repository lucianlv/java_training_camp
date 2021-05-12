package pers.cocoadel.configuration.converters;

public class IntegerConverter extends AbstractConverter<Integer> {

    @Override
    protected Integer doConvert(String value) {
        return Integer.valueOf(value);
    }
}
