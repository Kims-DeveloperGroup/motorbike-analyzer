package com.devoo.motorbike.analyzer.service;

import com.devoo.motorbike.analyzer.MotorbikeAnalysisApplication;
import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.crawler.NaverCafeItemCrawler;
import com.devoo.motorbike.analyzer.publish.NaverDocumentProcessor;
import com.devoo.motorbike.analyzer.repository.SaleItemRepository;
import com.devoo.motorbike.analyzer.repository.naver.AsyncNaverItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class NaverItemAnalysisService {

    private final AsyncNaverItemRepository asyncNaverItemRepository;
    private final SaleItemRepository saleItemRepository;
    private final NaverCafeItemCrawler naverCafeItemCrawler;
    private final NaverDocumentProcessor naverDocumentProcessor;

    @Autowired
    public NaverItemAnalysisService(AsyncNaverItemRepository asyncNaverItemRepository,
                                    SaleItemRepository saleItemRepository,
                                    NaverCafeItemCrawler naverCafeItemCrawler,
                                    NaverDocumentProcessor naverDocumentProcessor) {
        this.asyncNaverItemRepository = asyncNaverItemRepository;
        this.saleItemRepository = saleItemRepository;
        this.naverCafeItemCrawler = naverCafeItemCrawler;
        this.naverDocumentProcessor = naverDocumentProcessor;
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MotorbikeAnalysisApplication.class);
        NaverItemAnalysisService naverItemAnalysisService = context.getBean(NaverItemAnalysisService.class);
        naverItemAnalysisService.startAnalysis();
    }

    public void startAnalysis() {
        AtomicInteger count = new AtomicInteger(0);
        asyncNaverItemRepository.findAll()
                .parallel()
                .map(naverCafeItemCrawler)
                .filter(naverDocumentWrapper -> naverDocumentWrapper.getStatus().equals(DocumentStatus.NORMAL))
                .map(naverDocumentProcessor)
                .filter(saleItem -> saleItem.getRawDocument() != null)
                .subscribe(resultItem -> {
                    saleItemRepository.save(resultItem);
                    log.debug("{}: saved analyzed item {}", count.incrementAndGet(), resultItem.getUrl());
                });
    }
}