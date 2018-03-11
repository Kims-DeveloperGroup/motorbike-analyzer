package com.devoo.motorbike.analyzer.crawler;

import com.devoo.motorbike.analyzer.config.naver.NaverWebDriverClientConfig;
import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {NaverCafeItemCrawler.class, NaverWebDriverClientConfig.class})
public class NaverCafeItemCrawlerIT {
    @Autowired
    private NaverCafeItemCrawler naverCafeItemCrawler;

    @Test
    public void shouldDocumentBeRetrieved_whenTargetItemUrlIsGiven() {
        //Given
        String targetCafeUrl = "http://cafe.naver.com/bikecargogo";
        String cafeTitle = "바이크튜닝매니아 [오토바이] [스쿠터] [이륜차] [바튜매 보험] : 네이버 카페";
        NaverItem naverItem = new NaverItem();
        naverItem.setLink(targetCafeUrl);
        naverItem.setTitle(cafeTitle);

        //When
        NaverDocumentWrapper naverDocumentWrapper = naverCafeItemCrawler.getDocument(naverItem);
        String title = naverDocumentWrapper.getDocument().title();

        //Then
        Assertions.assertThat(title).isEqualToIgnoringCase(cafeTitle);
    }
}