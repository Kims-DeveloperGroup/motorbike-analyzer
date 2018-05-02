package com.devoo.motorbike.analyzer.processor.parser;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class YearParserTest {
    private final YearParser yearParser = new YearParser();

    @Test
    public void shouldBe4DigitNumberReturned_whenGivenTextContains4DigitNumber() {
        //Given
        String textWith4DigitNumber = "연식: 2014 입니다.";
        int expectedYear = 2014;

        //When
        Integer actualYearFromText = yearParser.parse(textWith4DigitNumber);

        //Then
        assertThat(actualYearFromText).isEqualTo(expectedYear);
    }

    @Test
    public void shouldBe4DigitNumberReturned_whenGivenTextContainsYearInTwoDigitAfter2000() {
        //Given
        String textWith4DigitNumber = "연식: 14년식 입니다.";
        int expectedYear = 2014;

        //When
        Integer actualYearFromText = yearParser.parse(textWith4DigitNumber);

        //Then
        assertThat(actualYearFromText).isEqualTo(expectedYear);
    }

    @Test
    public void shouldBe4DigitNumberReturned_whenGivenTextContainsYearInTwoDigitIn200X() {
        //Given
        String textWith4DigitNumber = "연식: 00년식 입니다.";
        int expectedYear = 2000;

        //When
        Integer actualYearFromText = yearParser.parse(textWith4DigitNumber);

        //Then
        assertThat(actualYearFromText).isEqualTo(expectedYear);
    }

    @Test
    public void shouldBe4DigitNumberReturned_whenGivenTextContainsYearInTwoDigitBefore2000() {
        //Given
        String textWith4DigitNumber = "연식: 38년식 입니다.";
        int expectedYear = 1938;

        //When
        Integer actualYearFromText = yearParser.parse(textWith4DigitNumber);

        //Then
        assertThat(actualYearFromText).isEqualTo(expectedYear);
    }
}