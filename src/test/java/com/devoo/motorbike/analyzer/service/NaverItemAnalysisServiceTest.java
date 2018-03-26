package com.devoo.motorbike.analyzer.service;

import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.crawler.NaverCafeItemCrawler;
import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.SaleItem;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import com.devoo.motorbike.analyzer.processor.NaverDocumentProcessor;
import com.devoo.motorbike.analyzer.publisher.TargetNaverItemPublisher;
import com.devoo.motorbike.analyzer.repository.SaleItemRepository;
import com.devoo.motorbike.analyzer.repository.naver.NaverItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NaverItemAnalysisServiceTest {
    @InjectMocks
    private NaverItemAnalysisService naverItemAnalysisService;

    @Mock
    private TargetNaverItemPublisher targetNaverItemPublisher;

    @Mock
    private NaverCafeItemCrawler naverCafeItemCrawler;

    @Mock
    private NaverDocumentProcessor naverDocumentProcessor;

    @Mock
    private SaleItemRepository saleItemRepository;

    @Mock
    private NaverItemRepository naverItemRepository;

    @Test
    public void shouldBeNaverItemsProcessedToResultItemAndSaved_whenNormalNaverItemsArePublished() throws InterruptedException {

        //Given
        NaverDocumentWrapper normalStatusDoc = new NaverDocumentWrapper(null, null);
        normalStatusDoc.setStatus(DocumentStatus.NORMAL);
        when(naverCafeItemCrawler.getDocuments(any())).thenReturn(Stream.of(normalStatusDoc));

        SaleItem expectedResult = new SaleItem();
        expectedResult.setRawDocument("<html></html>");
        expectedResult.setUrl("http://localhost:8080");
        when(naverDocumentProcessor.apply(normalStatusDoc)).thenReturn(expectedResult);

        //When
        naverItemAnalysisService.startAnalysis();

        //Then
        verify(saleItemRepository, times(1)).save(any(SaleItem.class));
    }

    @Test
    public void shouldBeNonExistingNaverItemsDeletedAndBeFilteredOutBeforeProcessing_whenDeletedNaverItemsArePublished() throws InterruptedException {

        //Given
        NaverItem naverItem = new NaverItem();
        naverItem.setLink("http://localhost:8080");
        NaverDocumentWrapper normalStatusDoc = new NaverDocumentWrapper(null, naverItem);
        normalStatusDoc.setStatus(DocumentStatus.DELETED);
        when(naverCafeItemCrawler.getDocuments(any())).thenReturn(Stream.of(normalStatusDoc));

        //When
        naverItemAnalysisService.startAnalysis();

        //Then
        verify(naverItemRepository, times(1)).delete(any(NaverItem.class));
        verify(saleItemRepository, times(0)).save(any(SaleItem.class));
    }

    @Test
    public void shouldBeCrawledNaverDocumentWrapperFilteredOut_whenNoDocumentIsRetrieved() throws InterruptedException {

        //Given
        NaverItem naverItem = new NaverItem();
        naverItem.setLink("http://localhost:8080");
        NaverDocumentWrapper normalStatusDoc = new NaverDocumentWrapper(null, naverItem);
        normalStatusDoc.setStatus(DocumentStatus.NORMAL);
        when(naverCafeItemCrawler.getDocuments(any())).thenReturn(Stream.of(normalStatusDoc));
        SaleItem expectedResult = new SaleItem();
        expectedResult.setUrl("http://localhost:8080");
        when(naverDocumentProcessor.apply(normalStatusDoc)).thenReturn(expectedResult);

        //When
        naverItemAnalysisService.startAnalysis();

        //Then
        verify(naverItemRepository, times(0)).delete(any(NaverItem.class));
        verify(naverDocumentProcessor, times(1)).apply(normalStatusDoc);
        verify(saleItemRepository, times(0)).save(any(SaleItem.class));
    }
}