package com.devoo.motorbike.analyzer.parser;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.SaleItem;
import org.springframework.stereotype.Component;

@Component
public class NaverDocumentParser {

    public SaleItem parseToSaleItem(NaverDocumentWrapper documentWrapper) {
        SaleItem saleItem = new SaleItem();
        saleItem.setTitle(documentWrapper.getDocument().title());
        return saleItem;
    }
}