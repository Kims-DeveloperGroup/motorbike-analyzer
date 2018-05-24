package com.devoo.motorbike.analyzer.service;

import com.devoo.motorbike.analyzer.MotorbikeAnalysisApplication;
import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.crawler.NaverCafeItemCrawler;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import com.devoo.motorbike.analyzer.processor.NaverProcessors;
import com.devoo.motorbike.analyzer.publisher.TargetNaverItemPublisher;
import com.devoo.motorbike.analyzer.repository.ResultItemRepository;
import com.devoo.motorbike.analyzer.repository.naver.NaverItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@SpringBootApplication
public class NaverItemAnalysisService {

    private final TargetNaverItemPublisher targetNaverItemPublisher;
    private final NaverItemRepository targetItemRepository;
    private final ResultItemRepository resultItemRepository;
    private final NaverCafeItemCrawler naverCafeItemCrawler;
    private final NaverProcessors naverProcessors;

    @Autowired
    public NaverItemAnalysisService(TargetNaverItemPublisher targetNaverItemPublisher,
                                    NaverItemRepository targetItemRepository,
                                    ResultItemRepository resultItemRepository,
                                    NaverCafeItemCrawler naverCafeItemCrawler,
                                    NaverProcessors naverProcessors) {
        this.targetNaverItemPublisher = targetNaverItemPublisher;
        this.targetItemRepository = targetItemRepository;
        this.resultItemRepository = resultItemRepository;
        this.naverCafeItemCrawler = naverCafeItemCrawler;
        this.naverCafeItemCrawler.setParallel(2);
        this.naverProcessors = naverProcessors;
    }

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = SpringApplication.run(MotorbikeAnalysisApplication.class);
        NaverItemAnalysisService naverItemAnalysisService = context.getBean(NaverItemAnalysisService.class);
        naverItemAnalysisService.startAnalysis();
    }

    private void deleteProcessedTargetItems() {
        resultItemRepository.findAll().forEach(item -> {
            targetItemRepository.deleteById(item.getId());
            log.debug("delete target item: {} from repository", item.getId());
        });
    }

    public void startAnalysis() throws InterruptedException {
        deleteProcessedTargetItems();
        BlockingQueue<TargetNaverItem> inputQueue = targetNaverItemPublisher.publishNaverItems();

        AtomicInteger count = new AtomicInteger(0);
        naverCafeItemCrawler.getDocuments(inputQueue)
                .map(naverDocumentWrapper -> {
                    if (naverDocumentWrapper.getStatus().equals(DocumentStatus.DELETED)) {
                        targetItemRepository.delete(naverDocumentWrapper.getTargetNaverItem());
                        log.debug("Deleted {} from repository", naverDocumentWrapper.getTargetNaverItem().getLink());
                    }
                    return naverDocumentWrapper;
                })
                .filter(naverDocumentWrapper -> naverDocumentWrapper.getStatus().equals(DocumentStatus.NORMAL))
                .map(naverProcessors)
                .filter(documentWrapper -> documentWrapper.getProcessedResults().size() > 0)
                .filter(documentWrapper -> documentWrapper.getDocument() != null)
                .forEach(resultItem -> {
                    resultItemRepository.save(resultItem);
                    log.debug("{}: saved analyzed item {}", count.incrementAndGet(), resultItem.getTargetNaverItem().getLink());
                });
        log.info("{} ends", this.getClass().getSimpleName());
    }
}