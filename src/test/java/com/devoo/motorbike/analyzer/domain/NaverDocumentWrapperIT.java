package com.devoo.motorbike.analyzer.domain;

import com.devoo.motorbike.analyzer.config.ElasticSearchConfig;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import com.devoo.motorbike.analyzer.repository.ResultItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Optional;

import static com.devoo.motorbike.analyzer.processor.naver.BatumaSaleItemProcessor.BatumaSaleItem;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ElasticSearchConfig.class, ResultItemRepository.class})
public class NaverDocumentWrapperIT {
    private TargetNaverItem targetNaverItem;
    private NaverDocumentWrapper naverDocumentWrapper;
    @Autowired
    private ResultItemRepository resultItemRepository;

    @Before
    public void setInitialTestCondition() {
        targetNaverItem = new TargetNaverItem();
        targetNaverItem.setLink("http://www.naver.com");
        naverDocumentWrapper = new NaverDocumentWrapper(null, targetNaverItem);

        BatumaSaleItem item = new BatumaSaleItem();
        naverDocumentWrapper.setProcessedResults(Arrays.asList(item));
        resultItemRepository.deleteById(naverDocumentWrapper.getId());
    }

    @After
    public void cleanTestSet() {
        resultItemRepository.deleteById(naverDocumentWrapper.getId());
    }

    @Test
    public void shouldBeItemSaved_whenAllFieldsAreFullySetToEntity() {
        // Given

        // When
        resultItemRepository.save(naverDocumentWrapper);

        // Then
        Optional<NaverDocumentWrapper> byId = resultItemRepository.findById(naverDocumentWrapper.getId());
        Assertions.assertThat(byId).isPresent();
    }
}