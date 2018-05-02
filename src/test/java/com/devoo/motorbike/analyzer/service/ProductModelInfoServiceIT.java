package com.devoo.motorbike.analyzer.service;

import com.devoo.motorbike.analyzer.config.ElasticSearchConfig;
import com.devoo.motorbike.analyzer.domain.Model;
import com.devoo.motorbike.analyzer.repository.paxxo.PaxxoModelRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductModelInfoService.class, PaxxoModelRepository.class, ElasticSearchConfig.class})
public class ProductModelInfoServiceIT {
    @Autowired
    private ProductModelInfoService productModelInfoService;

    @Test
    public void shouldBeMatchedModelReturned_whenPartOfNameIsGiven() {
        //Given
        String partOfModelName = "600rr";

        //When
        Optional<Model> matchedModelByName = productModelInfoService.findMatchedModelByName(partOfModelName);

        //Then
        String expectedModelName = "CBR600RR";
        assertThat(matchedModelByName.get().getName()).isEqualTo(expectedModelName);
    }

    @Test
    public void shouldBeNullReturned_whenNoMatchedModelExists() {
        //Given
        String nonExistingName = "unknown";

        //When
        Optional<Model> matchedModelByName = productModelInfoService.findMatchedModelByName(nonExistingName);

        //Then
        assertThat(matchedModelByName.isPresent()).isFalse();
    }
}