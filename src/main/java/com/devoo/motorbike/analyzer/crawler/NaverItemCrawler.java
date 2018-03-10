package com.devoo.motorbike.analyzer.crawler;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import com.devoo.naverlogin.NaverPageCrawler;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static com.devoo.motorbike.analyzer.constants.DocumentStatus.DELETED;
import static com.devoo.motorbike.analyzer.constants.DocumentStatus.NORMAL;

/**
 * Crawls the page of naver item.
 */
@Component
@Slf4j
public class NaverItemCrawler implements Function<NaverItem, NaverDocumentWrapper> {

    private final NaverPageCrawler naverPageCrawler;
    private final WebDriver naverLogin;
    public static final String DELETED_POST_ALERT_MESSAGE = "삭제되었거나 없는 게시글입니다";


    @Autowired
    public NaverItemCrawler(WebDriver naverLogin) {
        this.naverLogin = naverLogin;
        naverPageCrawler = new NaverPageCrawler();

    }

    /**
     * Gets a document of a given url
     *
     * @param naverItem to crawl
     * @return document parsed from page source.
     */
    public NaverDocumentWrapper getDocument(NaverItem naverItem) {
        NaverDocumentWrapper naverDocumentWrapper;
        String url = naverItem.getLink();
        try {
            Document document = naverPageCrawler.getDocument(naverLogin, url);
            document.setBaseUri(url);
            naverDocumentWrapper = new NaverDocumentWrapper(document);
            naverDocumentWrapper.setStatus(NORMAL);
            log.debug("Crawling complete: {}", url);
        } catch (UnhandledAlertException e) {
            dismissAlert();
            naverDocumentWrapper = new NaverDocumentWrapper(null);
            String exceptionMessage = e.getLocalizedMessage();
            log.debug("Exception: {}, message: {} ", url, exceptionMessage);
            if (exceptionMessage.contains(DELETED_POST_ALERT_MESSAGE)) {
                naverDocumentWrapper.setStatus(DELETED);
                log.debug("Deleted naver post: {}", url);
            }
        }
        return naverDocumentWrapper;
    }

    private void dismissAlert() {
        naverLogin.switchTo().alert().dismiss();
    }

    @Override
    public NaverDocumentWrapper apply(NaverItem naverItem) {
        log.debug("Crawling doc : {}", naverItem.getLink());
        return getDocument(naverItem);
    }
}
