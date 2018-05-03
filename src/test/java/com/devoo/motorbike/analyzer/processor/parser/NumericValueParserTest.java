package com.devoo.motorbike.analyzer.processor.parser;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class NumericValueParserTest {
    public static final NumericValueParser numericValueParser = new NumericValueParser();

    @Test
    public void shouldBeNumericValueReturned_whenTheGivenValueIsCommaSeparated() {
        //Given
        String commaSeparateValue = "1,000,000원";

        //When
        Long actual = numericValueParser.parse(commaSeparateValue);

        //Then
        assertThat(actual).isEqualTo(1000000);
    }

    @Test
    public void shouldBeNonPriceValueExcluded_whenNumberingTagIsIncludedInTheGivenText_case1() {
        //Given
        String commaSeparateValue = "1.가격 1,000,000원";

        //When
        Long actual = numericValueParser.parse(commaSeparateValue);

        //Then
        assertThat(actual).isEqualTo(1000000);
    }

    @Test
    public void shouldBeNonPriceValueExcluded_whenNumberingTagIsIncludedInTheGivenText_case2() {
        //Given
        String commaSeparateValue = "1] 가격 1,000,000원";

        //When
        Long actual = numericValueParser.parse(commaSeparateValue);

        //Then
        assertThat(actual).isEqualTo(1000000);
    }
}