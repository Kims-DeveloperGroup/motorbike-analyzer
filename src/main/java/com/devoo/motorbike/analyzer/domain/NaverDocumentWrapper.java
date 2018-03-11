package com.devoo.motorbike.analyzer.domain;

import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import lombok.Data;
import org.jsoup.nodes.Document;

@Data
public class NaverDocumentWrapper {
    private final NaverItem naverItem;
    private final Document document;
    private DocumentStatus status;

    public NaverDocumentWrapper(Document document, NaverItem naverItem) {
        this.document = document;
        this.naverItem = naverItem;
    }
}