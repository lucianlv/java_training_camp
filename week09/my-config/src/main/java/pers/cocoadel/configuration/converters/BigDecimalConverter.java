package pers.cocoadel.configuration.converters;

import java.math.BigDecimal;

public class BigDecimalConverter extends AbstractConverter<BigDecimal> {

    @Override
    protected BigDecimal doConvert(String value) {
        return new BigDecimal(value);
    }
}
