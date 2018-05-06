package com.devoo.motorbike.analyzer.processor.parser;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ModelNameParserTest {
    private static final ModelNameParser modelNameParser = new ModelNameParser();

    @Test
    public void shouldBeModelNameReturned_whenModelNameIsFullyCombinedInEnglishAndNumberValue_case1() {
        //Given
        String expectedModelName = "honda cbr 125";
        String englishAndNumberCombinedModel = "1. 모델명은 ,," + expectedModelName + " 입니다.";
        //When
        String actualModelNameFromText = modelNameParser.parse(englishAndNumberCombinedModel);

        //Then
        assertThat(actualModelNameFromText).isEqualTo(expectedModelName);
    }

    @Test
    public void shouldBeModelNameReturned_whenModelNameIsFullyCombinedInEnglishAndNumberValue_case2() {
        // Given
        String rawText = "1. 제작사, 모델명 : 야마하R6";

        // When
        String parsed = modelNameParser.parse(rawText);

        //Then
        String expectedModelName = "R6";
        assertThat(parsed).isEqualTo(expectedModelName);
    }

}