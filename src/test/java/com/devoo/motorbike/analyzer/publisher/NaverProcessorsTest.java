package com.devoo.motorbike.analyzer.publisher;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import com.devoo.motorbike.analyzer.processor.NaverProcessors;
import com.devoo.motorbike.analyzer.processor.naver.BatumaSaleItemProcessor;
import com.devoo.motorbike.analyzer.processor.naver.NaverCafeDocumentRefiner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.assertj.core.api.Assertions;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NaverProcessorsTest {
    @InjectMocks
    private NaverProcessors naverProcessors;

    @Mock
    private NaverCafeDocumentRefiner documentRefiner;

    @Mock
    private BatumaSaleItemProcessor processor;

    private Gson gson = new Gson();

    @Test
    public void shouldResultItemHasProcessedItem_whenProcessedItemsReturnedFromProcessor() {
        //Given
        String pageUrl = "https://www.naver.com";
        TargetNaverItem targetNaverItem = new TargetNaverItem();
        targetNaverItem.setLink(pageUrl);
        Document document = new Document(pageUrl);
        NaverDocumentWrapper documentWrapper = new NaverDocumentWrapper(document, targetNaverItem);
        when(documentRefiner.execute(documentWrapper)).thenReturn(documentWrapper);
        JsonObject fromProcessor = new JsonObject();
        fromProcessor.addProperty("result", "resultValue");
        when(processor.execute(documentWrapper)).thenReturn(fromProcessor);

        //When
        NaverDocumentWrapper processed = naverProcessors.apply(documentWrapper);

        //Then
        Assertions.assertThat(processed.getProcessedResults().size()).isGreaterThan(0);
    }
}