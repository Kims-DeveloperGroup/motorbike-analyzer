package com.devoo.motorbike.analyzer.processor.parser;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class YearParser implements Parser<Integer> {
    public final Pattern NUMERIC_PATTERN_OF_RELEASED_YEAR = Pattern.compile("[0-9]{4}|[0-9]{2}");
    private String currentDecade = String.valueOf(LocalDate.now().getYear()).substring(2, 3);
    public final Pattern TWO_DIGIT_YEAR_PATTER_AFTER_2000 = Pattern.compile("[0-" + currentDecade + "][0-9]");

    @Override
    public Integer parse(String text) {
        Matcher numericValueMatcher = NUMERIC_PATTERN_OF_RELEASED_YEAR.matcher(text);
        if (numericValueMatcher.find()) {
            String year = numericValueMatcher.group();
            if (year.length() == 2) {
                if (TWO_DIGIT_YEAR_PATTER_AFTER_2000.matcher(year).matches()) {
                    return Integer.valueOf("20" + year);
                } else {
                    return Integer.valueOf("19" + year);
                }
            }
            return Integer.valueOf(year);
        }
        return null;
    }
}