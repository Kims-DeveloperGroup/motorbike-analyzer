package com.devoo.motorbike.analyzer.processor;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.ProcessResult;
import com.devoo.motorbike.analyzer.processor.naver.BatumaSaleItemProcessor;
import com.devoo.motorbike.analyzer.processor.naver.NaverCafeDocumentRefiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
public class NaverProcessors implements Function<NaverDocumentWrapper, NaverDocumentWrapper> {
    private final NaverCafeDocumentRefiner cafeDocumentRefiner;
    private List<Processor<NaverDocumentWrapper, ? extends ProcessResult>> processors = new LinkedList<>();

    @Autowired
    public NaverProcessors(NaverCafeDocumentRefiner cafeDocumentRefiner,
                           BatumaSaleItemProcessor batumaSaleItemProcessor) {
        this.cafeDocumentRefiner = cafeDocumentRefiner;
        processors.add(batumaSaleItemProcessor);
    }

    @Override
    public NaverDocumentWrapper apply(NaverDocumentWrapper naverDocumentWrapper) {
        naverDocumentWrapper = cafeDocumentRefiner.execute(naverDocumentWrapper);
        naverDocumentWrapper.convertDocumentToText();
        return executeProcessors(naverDocumentWrapper);
    }

    public NaverDocumentWrapper executeProcessors(NaverDocumentWrapper naverDocumentWrapper) {
        log.debug("Processing item...{}", naverDocumentWrapper.getDocument().baseUri());
        processors.forEach(processor -> {
            try {
                Optional.ofNullable(processor.execute(naverDocumentWrapper))
                        .ifPresent(processedResult -> naverDocumentWrapper.addProcessedResult(processedResult));
            } catch (Exception e) {
                log.error("Exception occurred while executing a processor for {}",
                        naverDocumentWrapper.getTargetNaverItem().getLink(), e);
            }
        });
        log.debug("Processed {}", naverDocumentWrapper.getTargetNaverItem().getLink());
        return naverDocumentWrapper;
    }
}