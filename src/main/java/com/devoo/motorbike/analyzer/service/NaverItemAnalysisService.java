package com.devoo.motorbike.analyzer.service;

import com.devoo.motorbike.analyzer.MotorbikeAnalysisApplication;
import com.devoo.motorbike.analyzer.crawler.NaverItemCrawler;
import com.devoo.motorbike.analyzer.publish.NaverDocumentProcessor;
import com.devoo.motorbike.analyzer.repository.SaleItemRepository;
import com.devoo.motorbike.analyzer.repository.naver.AsyncNaverItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NaverItemAnalysisService {

    private final AsyncNaverItemRepository asyncNaverItemRepository;
    private final SaleItemRepository saleItemRepository;
    private final NaverItemCrawler naverItemCrawler;
    private final NaverDocumentProcessor naverDocumentProcessor;

    @Autowired
    public NaverItemAnalysisService(AsyncNaverItemRepository asyncNaverItemRepository,
                                    SaleItemRepository saleItemRepository,
                                    NaverItemCrawler naverItemCrawler,
                                    NaverDocumentProcessor naverDocumentProcessor) {
        this.asyncNaverItemRepository = asyncNaverItemRepository;
        this.saleItemRepository = saleItemRepository;
        this.naverItemCrawler = naverItemCrawler;
        this.naverDocumentProcessor = naverDocumentProcessor;
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MotorbikeAnalysisApplication.class);
        NaverItemAnalysisService naverItemAnalysisService = context.getBean(NaverItemAnalysisService.class);
        naverItemAnalysisService.startAnalysis();
    }

    public void startAnalysis() {
        asyncNaverItemRepository.findAll()
                .parallel()
                .map(naverItemCrawler)
                .map(naverDocumentProcessor)
                .subscribe(resultItem -> {
                    saleItemRepository.save(resultItem);
                    log.debug("saved analyzed item {}", resultItem);
                });
    }
}