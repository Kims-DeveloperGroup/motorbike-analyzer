package com.devoo.motorbike.analyzer.crawler;

import com.devoo.motorbike.analyzer.config.naver.NaverLoginWebDriverConfig;
import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {NaverItemCrawler.class, NaverLoginWebDriverConfig.class})
public class NaverItemCrawlerIT {
    @Autowired
    private NaverItemCrawler naverItemCrawler;

    @Test
    public void shouldDocumentBeRetrieved_whenTargetItemUrlIsGiven() {
        //Given
        String targetUrl = "https://www.naver.com";
        String naverTitle = "NAVER";
        NaverItem naverItem = new NaverItem();
        naverItem.setLink(targetUrl);
        naverItem.setTitle(naverTitle);

        //When
        NaverDocumentWrapper naverDocumentWrapper = naverItemCrawler.getDocument(naverItem);
        String title = naverDocumentWrapper.getDocument().title();

        //Then
        Assertions.assertThat(title).isEqualToIgnoringCase(naverTitle);
    }
}