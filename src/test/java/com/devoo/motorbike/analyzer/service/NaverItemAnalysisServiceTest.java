package com.devoo.motorbike.analyzer.service;

import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.crawler.NaverCafeItemCrawler;
import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import com.devoo.motorbike.analyzer.processor.NaverProcessors;
import com.devoo.motorbike.analyzer.processor.naver.BatumaSaleItemProcessor.BatumaSaleItem;
import com.devoo.motorbike.analyzer.publisher.TargetNaverItemPublisher;
import com.devoo.motorbike.analyzer.repository.ResultItemRepository;
import com.devoo.motorbike.analyzer.repository.naver.NaverItemRepository;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
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
    private NaverProcessors naverProcessors;

    @Mock
    private ResultItemRepository resultItemRepository;

    @Mock
    private NaverItemRepository naverItemRepository;

    @Test
    public void shouldBeNaverItemsProcessedToResultItem_whenNormalStatusTargetItemIsCrawled() throws InterruptedException {

        //Given
        TargetNaverItem targetNaverItem = new TargetNaverItem();
        targetNaverItem.setLink("http://www.naver.com");
        NaverDocumentWrapper normalStatusDocWrapper = new NaverDocumentWrapper(null, targetNaverItem);
        normalStatusDocWrapper.setStatus(DocumentStatus.NORMAL);
        normalStatusDocWrapper.setDocument(Jsoup.parse("<html>test</html>"));
        when(naverCafeItemCrawler.getDocuments(any())).thenReturn(Stream.of(normalStatusDocWrapper));
        when(naverProcessors.apply(normalStatusDocWrapper)).thenReturn(normalStatusDocWrapper);

        //When
        naverItemAnalysisService.startAnalysis();

        //Then
        verify(naverProcessors, times(1)).apply(normalStatusDocWrapper);
    }

    @Test
    public void shouldBeResultItemNotSaved_whenProcessedResultNotExists() throws InterruptedException {

        //Given
        TargetNaverItem targetNaverItem = new TargetNaverItem();
        targetNaverItem.setLink("http://www.naver.com");
        NaverDocumentWrapper normalStatusDocWrapper = new NaverDocumentWrapper(null, targetNaverItem);
        normalStatusDocWrapper.setStatus(DocumentStatus.NORMAL);
        normalStatusDocWrapper.setDocument(Jsoup.parse("<html>test</html>"));
        normalStatusDocWrapper.setProcessedResults(Arrays.asList());
        when(naverCafeItemCrawler.getDocuments(any())).thenReturn(Stream.of(normalStatusDocWrapper));
        when(naverProcessors.apply(normalStatusDocWrapper)).thenReturn(normalStatusDocWrapper);

        //When
        naverItemAnalysisService.startAnalysis();

        //Then
        verify(naverProcessors, times(1)).apply(normalStatusDocWrapper);
        verify(resultItemRepository, never()).save(normalStatusDocWrapper);
    }

    @Test
    public void shouldBeResultItemSaved_whenProcessedResultExists() throws InterruptedException {

        //Given
        TargetNaverItem targetNaverItem = new TargetNaverItem();
        targetNaverItem.setLink("http://www.naver.com");
        NaverDocumentWrapper normalStatusDocWrapper = new NaverDocumentWrapper(null, targetNaverItem);
        normalStatusDocWrapper.setStatus(DocumentStatus.NORMAL);
        normalStatusDocWrapper.setDocument(Jsoup.parse("<html>test</html>"));
        List<Object> processedResults = Arrays.asList(new BatumaSaleItem());
        normalStatusDocWrapper.setProcessedResults(processedResults);
        when(naverCafeItemCrawler.getDocuments(any())).thenReturn(Stream.of(normalStatusDocWrapper));
        when(naverProcessors.apply(normalStatusDocWrapper)).thenReturn(normalStatusDocWrapper);

        //When
        naverItemAnalysisService.startAnalysis();

        //Then
        verify(naverProcessors, times(1)).apply(normalStatusDocWrapper);
        verify(resultItemRepository, times(1)).save(normalStatusDocWrapper);
    }


    @Test
    public void shouldBeNonExistingNaverItemsDeletedAndBeFilteredOutBeforeProcessing_whenDeletedNaverItemsArePublished() throws InterruptedException {

        //Given
        TargetNaverItem targetNaverItem = new TargetNaverItem();
        targetNaverItem.setLink("http://localhost:8080");
        NaverDocumentWrapper normalStatusDoc = new NaverDocumentWrapper(null, targetNaverItem);
        normalStatusDoc.setStatus(DocumentStatus.DELETED);
        when(naverCafeItemCrawler.getDocuments(any())).thenReturn(Stream.of(normalStatusDoc));

        //When
        naverItemAnalysisService.startAnalysis();

        //Then
        verify(naverItemRepository, times(1)).delete(any(TargetNaverItem.class));
        verify(resultItemRepository, times(0)).save(any(NaverDocumentWrapper.class));
    }

    @Test
    public void shouldBeCrawledNaverDocumentWrapperFilteredOut_whenNoDocumentIsRetrieved() throws InterruptedException {

        //Given
        TargetNaverItem targetNaverItem = new TargetNaverItem();
        targetNaverItem.setLink("http://localhost:8080");
        NaverDocumentWrapper normalStatusDoc = new NaverDocumentWrapper(null, targetNaverItem);
        normalStatusDoc.setStatus(DocumentStatus.NORMAL);
        when(naverCafeItemCrawler.getDocuments(any())).thenReturn(Stream.of(normalStatusDoc));

        NaverDocumentWrapper expectedResult = normalStatusDoc;
        when(naverProcessors.apply(normalStatusDoc)).thenReturn(expectedResult);

        //When
        naverItemAnalysisService.startAnalysis();

        //Then
        verify(naverItemRepository, times(0)).delete(any(TargetNaverItem.class));
        verify(naverProcessors, times(1)).apply(normalStatusDoc);
        verify(resultItemRepository, times(0)).save(any(NaverDocumentWrapper.class));
    }
}