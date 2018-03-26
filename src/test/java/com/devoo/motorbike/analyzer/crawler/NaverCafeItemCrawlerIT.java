package com.devoo.motorbike.analyzer.crawler;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {NaverCafeItemCrawler.class})
public class NaverCafeItemCrawlerIT {
    @Autowired
    private NaverCafeItemCrawler naverCafeItemCrawler;

    @Test
    public void shouldDocumentBeRetrieved_whenTargetItemUrlIsGiven() throws InterruptedException {
        //Given
        String targetCafeUrl = "http://cafe.naver.com/bikecargogo";
        String cafeTitle = "바이크튜닝매니아 [오토바이] [스쿠터] [이륜차] [바튜매 보험] : 네이버 카페";
        NaverItem naverItem = new NaverItem();
        naverItem.setLink(targetCafeUrl);
        naverItem.setTitle(cafeTitle);
        BlockingQueue<NaverItem> items = new LinkedBlockingQueue<>();
        items.put(naverItem);
        naverCafeItemCrawler.setParallel(1);
        //When
        Optional<NaverDocumentWrapper> naverDocumentWrapper = naverCafeItemCrawler.getDocuments(items).findFirst();
        String title = naverDocumentWrapper.get().getDocument().title();

        //Then
        Assertions.assertThat(title).isEqualToIgnoringCase(cafeTitle);
    }
}