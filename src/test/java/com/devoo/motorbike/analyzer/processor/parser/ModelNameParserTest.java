package com.devoo.motorbike.analyzer.processor.parser;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ModelNameParserTest {
    private static final ModelNameParser modelNameParser = new ModelNameParser();

    @Test
    public void shouldBeModelNameReturned_whenModelNameIsFullyCombinedInEnglishAndNumberValue() {
        //Given
        String expectedModelName = "honda cbr 125";
        String englishAndNumberCombinedModel = "1. 모델명은 ,," + expectedModelName + " 입니다.";
        //When
        String actualModelNameFromText = modelNameParser.parse(englishAndNumberCombinedModel);

        //Then
        assertThat(actualModelNameFromText).isEqualTo(expectedModelName);
    }

}