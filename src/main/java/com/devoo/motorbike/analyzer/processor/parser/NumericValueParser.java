package com.devoo.motorbike.analyzer.processor.parser;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NumericValueParser implements Parser<Long> {
    public static final Pattern NUMERIC = Pattern.compile("([0-9])");
    public static final Pattern EXCLUDED_NUMBER_TAG_PATTERN = Pattern.compile("[0-9]+[/.)>:\\]]");

    @Override
    public Long parse(String text) {
        text = excludeNonPriceNumberCharacters(text);
        StringBuilder numberBuilder = new StringBuilder();
        Matcher numericPatternMatcher = NUMERIC.matcher(text);
        while (numericPatternMatcher.find()) {
            numberBuilder.append(numericPatternMatcher.group());
        }
        if (numberBuilder.length() > 0) {
            return Long.valueOf(numberBuilder.toString());
        }
        return null;
    }

    /**
     * Exclude numeric characters that is not included in price value.
     *
     * @param text
     * @return text excluding non price number chars
     */
    private String excludeNonPriceNumberCharacters(String text) {
        return EXCLUDED_NUMBER_TAG_PATTERN.matcher(text).replaceAll("");
    }
}
