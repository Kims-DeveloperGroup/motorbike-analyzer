package com.devoo.motorbike.analyzer.processor;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.SaleItem;
import com.devoo.motorbike.analyzer.processor.parser.NaverDocumentParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Slf4j
public class NaverProcessors implements Function<NaverDocumentWrapper, SaleItem> {

    private final NaverDocumentParser naverDocumentParser;

    @Autowired
    public NaverProcessors(NaverDocumentParser naverDocumentParser) {
        this.naverDocumentParser = naverDocumentParser;
    }

    @Override
    public SaleItem apply(NaverDocumentWrapper document) {
        SaleItem saleItem = process(document);
        return saleItem;
    }

    public SaleItem process(NaverDocumentWrapper document) {
        log.debug("Processing item...{}", document.getDocument().baseUri());
        return naverDocumentParser.parseToSaleItem(document);
    }
}