package com.devoo.motorbike.analyzer.crawler;

import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import com.devoo.naverlogin.NaverPageCrawler;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.Alert;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NaverItemCrawlerTest {
    @InjectMocks
    private NaverItemCrawler naverItemCrawler;

    @Mock
    private WebDriver naverLogin;

    @Mock
    private NaverPageCrawler naverPageCrawler;

    @Mock
    private WebDriver.TargetLocator targetLocator;

    @Test
    public void shouldBeDocumentMarkedAsDeleted_whenDeletedAlertShows() {
        //Given
        NaverItem naverItem = new NaverItem();
        naverItem.setLink("http://www.naver.com");
        UnhandledAlertException unhandledAlertException =
                new UnhandledAlertException(NaverItemCrawler.DELETED_POST_ALERT_MESSAGE);
        Mockito.doThrow(unhandledAlertException).when(naverLogin)
                .get(naverItem.getLink());
        when(naverLogin.switchTo()).thenReturn(new MockTargetLocator());

        //When
        NaverDocumentWrapper naverDocumentWrapper = naverItemCrawler.getDocument(naverItem);
        DocumentStatus documentStatus = naverDocumentWrapper.getStatus();

        //Then
        Assertions.assertThat(documentStatus).isEqualTo(DocumentStatus.DELETED);
    }

    private class MockTargetLocator implements WebDriver.TargetLocator {

        @Override
        public Alert alert() {

            return new MockAlert();
        }

        @Override
        public WebDriver frame(int index) {
            return null;
        }

        @Override
        public WebDriver frame(String nameOrId) {
            return null;
        }

        @Override
        public WebDriver frame(WebElement frameElement) {
            return null;
        }

        @Override
        public WebDriver parentFrame() {
            return null;
        }

        @Override
        public WebDriver window(String nameOrHandle) {
            return null;
        }

        @Override
        public WebDriver defaultContent() {
            return null;
        }

        @Override
        public WebElement activeElement() {
            return null;
        }
    }

    private class MockAlert implements Alert {

        @Override
        public void dismiss() {

        }

        @Override
        public void accept() {

        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public void sendKeys(String keysToSend) {

        }
    }
}