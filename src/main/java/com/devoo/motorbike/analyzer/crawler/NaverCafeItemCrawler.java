package com.devoo.motorbike.analyzer.crawler;

import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import com.devoo.naverlogin.NaverClient;
import com.devoo.naverlogin.ParallelNaverClient;
import com.devoo.naverlogin.runner.ClientAction;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriverException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import static com.devoo.motorbike.analyzer.constants.DocumentStatus.DELETED;
import static com.devoo.motorbike.analyzer.constants.DocumentStatus.NORMAL;

/**
 * Crawls the page of naver item.
 */
@Component
@Slf4j
public class NaverCafeItemCrawler {

    public static final String DELETED_POST_ALERT_MESSAGE = "삭제되었거나 없는 게시글입니다";
    public static final String CAFE_CONTENT_IFRAME_NAME = "cafe_main";
    private int parallel;

    @Value("${naver.userId}")
    private String userId;

    @Value("${naver.password}")
    private String password;

    public NaverCafeItemCrawler() {
        this.parallel = 1;
    }

    /**
     * Gets documents of input items.
     */
    public Stream<NaverDocumentWrapper> getDocuments(BlockingQueue<TargetNaverItem> inputQueue) throws InterruptedException {
        ParallelNaverClient<TargetNaverItem, NaverDocumentWrapper> parallelNaverClient = new ParallelNaverClient<>(this.parallel);
        parallelNaverClient.tryToLogin(this.userId, this.password);
        return parallelNaverClient.startAsynchronously(new NaverItemCrawlingAction(), inputQueue);
    }

    public void setParallel(int parallel) {
        this.parallel = parallel;
    }

    public static class NaverItemCrawlingAction implements ClientAction<TargetNaverItem, NaverDocumentWrapper> {

        @Override
        public NaverDocumentWrapper apply(TargetNaverItem item, NaverClient client) {
            NaverDocumentWrapper naverDocumentWrapper;
            String url = item.getLink();
            try {
                Document document = client.getIframe(url, CAFE_CONTENT_IFRAME_NAME);
                naverDocumentWrapper = new NaverDocumentWrapper(document, item);
                naverDocumentWrapper.setStatus(NORMAL);
                log.debug("Crawling complete: {}", url);
            } catch (UnhandledAlertException e) {
                client.closeAlert();
                naverDocumentWrapper = new NaverDocumentWrapper(null, item);
                String exceptionMessage = e.getLocalizedMessage();
                log.debug("Exception: {}, message: {} ", url, exceptionMessage);
                if (exceptionMessage.contains(DELETED_POST_ALERT_MESSAGE)) {
                    naverDocumentWrapper.setStatus(DELETED);
                    log.debug("Naver item is deleted: {}", url);
                }
            } catch (WebDriverException e) {
                naverDocumentWrapper = new NaverDocumentWrapper(null, item);
                naverDocumentWrapper.setStatus(DocumentStatus.EXCEPTIONAL);
            }
            return naverDocumentWrapper;
        }

    }
}
