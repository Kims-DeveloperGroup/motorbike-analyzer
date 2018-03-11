package com.devoo.motorbike.analyzer.crawler;

import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import com.devoo.naverlogin.NaverClient;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.UnhandledAlertException;

import static org.mockito.Mockito.doNothing;

@RunWith(MockitoJUnitRunner.class)
public class NaverCafeItemCrawlerTest {
    @InjectMocks
    private NaverCafeItemCrawler naverCafeItemCrawler;

    @Mock
    private NaverClient naverClient;

    @Test
    public void shouldBeDocumentMarkedAsDeleted_whenDeletedAlertShows() {
        //Given
        NaverItem naverItem = new NaverItem();
        naverItem.setLink("http://cafe.naver.com/bikecargogo");
        UnhandledAlertException unhandledAlertException =
                new UnhandledAlertException(NaverCafeItemCrawler.DELETED_POST_ALERT_MESSAGE);
        Mockito.doThrow(unhandledAlertException).when(naverClient)
                .getIframe(naverItem.getLink(), NaverCafeItemCrawler.CAFE_CONTENT_IFRAME_NAME);
        doNothing().when(naverClient).closeAlert();

        //When
        NaverDocumentWrapper naverDocumentWrapper = naverCafeItemCrawler.getDocument(naverItem);
        DocumentStatus documentStatus = naverDocumentWrapper.getStatus();

        //Then
        Assertions.assertThat(documentStatus).isEqualTo(DocumentStatus.DELETED);
    }
}