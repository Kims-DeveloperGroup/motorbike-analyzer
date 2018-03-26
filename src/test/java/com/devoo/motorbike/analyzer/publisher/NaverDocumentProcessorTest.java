package com.devoo.motorbike.analyzer.publisher;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.SaleItem;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import com.devoo.motorbike.analyzer.parser.NaverDocumentParser;
import com.devoo.motorbike.analyzer.processor.NaverDocumentProcessor;
import org.assertj.core.api.Assertions;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NaverDocumentProcessorTest {
    @InjectMocks
    private NaverDocumentProcessor itemProcessor;

    @Mock
    private NaverDocumentParser naverDocumentParser;

    @Test
    public void shouldCrawledDocumentHasSameTitleAsTheGivenDocument_whenProcessingDocument() {
        //Given
        String pageUrl = "https://www.naver.com";
        NaverItem naverItem = new NaverItem();
        naverItem.setLink(pageUrl);
        Document document = new Document(pageUrl);
        SaleItem saleItem = new SaleItem();
        saleItem.setUrl(pageUrl);
        NaverDocumentWrapper documentWrapper = new NaverDocumentWrapper(document, naverItem);
        Mockito.when(naverDocumentParser.parseToSaleItem(documentWrapper))
                .thenReturn(saleItem);
        //When
        SaleItem processed = itemProcessor.apply(documentWrapper);
        String processedPageUrl = processed.getUrl();

        //Then
        Assertions.assertThat(processedPageUrl).isEqualTo(pageUrl);
    }
}