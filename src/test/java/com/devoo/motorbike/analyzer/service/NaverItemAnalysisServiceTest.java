package com.devoo.motorbike.analyzer.service;

import com.devoo.motorbike.analyzer.crawler.NaverCafeItemCrawler;
import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.SaleItem;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import com.devoo.motorbike.analyzer.publish.NaverDocumentProcessor;
import com.devoo.motorbike.analyzer.repository.SaleItemRepository;
import com.devoo.motorbike.analyzer.repository.naver.AsyncNaverItemRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NaverItemAnalysisServiceTest {
    @InjectMocks
    private NaverItemAnalysisService naverItemAnalysisService;

    @Mock
    private AsyncNaverItemRepository asyncNaverItemRepository;

    @Mock
    private NaverCafeItemCrawler naverCafeItemCrawler;

    @Mock
    private NaverDocumentProcessor naverDocumentProcessor;

    @Mock
    private SaleItemRepository saleItemRepository;

    @Test
    @Ignore
    public void shouldBeProvidedNaverItemsSaved_whenResultItemsArePublished() {
        //Given
        NaverItem[] items = new NaverItem[]{new NaverItem(), new NaverItem()};
        when(asyncNaverItemRepository.findAll()).thenReturn(Flux.fromArray(items));
        when(naverCafeItemCrawler.getDocument(any(NaverItem.class)))
                .thenReturn(new NaverDocumentWrapper(null));
        when(naverDocumentProcessor.process(any(NaverDocumentWrapper.class)))
                .thenReturn(new SaleItem());
//        doNothing().when(saleItemRepository).save(any(SaleItem.class));
        naverItemAnalysisService.startAnalysis();
//
//        //Then
    }
}