package com.devoo.motorbike.analyzer.service;

import com.devoo.motorbike.analyzer.MotorbikeAnalysisApplication;
import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.crawler.NaverCafeItemCrawler;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import com.devoo.motorbike.analyzer.processor.NaverDocumentProcessor;
import com.devoo.motorbike.analyzer.publisher.TargetNaverItemPublisher;
import com.devoo.motorbike.analyzer.repository.SaleItemRepository;
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
    private final SaleItemRepository saleItemRepository;
    private final NaverCafeItemCrawler naverCafeItemCrawler;
    private final NaverDocumentProcessor naverDocumentProcessor;

    @Autowired
    public NaverItemAnalysisService(TargetNaverItemPublisher targetNaverItemPublisher,
                                    NaverItemRepository targetItemRepository,
                                    SaleItemRepository saleItemRepository,
                                    NaverCafeItemCrawler naverCafeItemCrawler,
                                    NaverDocumentProcessor naverDocumentProcessor) {
        this.targetNaverItemPublisher = targetNaverItemPublisher;
        this.targetItemRepository = targetItemRepository;
        this.saleItemRepository = saleItemRepository;
        this.naverCafeItemCrawler = naverCafeItemCrawler;
        this.naverCafeItemCrawler.setParallel(3);
        this.naverDocumentProcessor = naverDocumentProcessor;
    }

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = SpringApplication.run(MotorbikeAnalysisApplication.class);
        NaverItemAnalysisService naverItemAnalysisService = context.getBean(NaverItemAnalysisService.class);
        naverItemAnalysisService.startAnalysis();
    }

    public void startAnalysis() throws InterruptedException {
        BlockingQueue<NaverItem> inputQueue = targetNaverItemPublisher.publishNaverItems();

        AtomicInteger count = new AtomicInteger(0);
        naverCafeItemCrawler.getDocuments(inputQueue)
                .map(naverDocumentWrapper -> {
                    if (naverDocumentWrapper.getStatus().equals(DocumentStatus.DELETED)) {
                        targetItemRepository.delete(naverDocumentWrapper.getNaverItem());
                        log.debug("Deleted {} from repository", naverDocumentWrapper.getNaverItem().getLink());
                    }
                    return naverDocumentWrapper;
                })
                .filter(naverDocumentWrapper -> naverDocumentWrapper.getStatus().equals(DocumentStatus.NORMAL))
                .map(naverDocumentProcessor)
                .filter(saleItem -> saleItem.getRawDocument() != null)
                .forEach(resultItem -> {
                    saleItemRepository.save(resultItem);
                    log.debug("{}: saved analyzed item {}", count.incrementAndGet(), resultItem.getUrl());
                });
    }
}