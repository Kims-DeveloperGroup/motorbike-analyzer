package com.devoo.motorbike.analyzer.domain;

import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import lombok.Data;
import org.jsoup.nodes.Document;

@Data
public class NaverDocumentWrapper {
    private final Document document;
    private DocumentStatus status;

    public NaverDocumentWrapper(Document document) {
        this.document = document;
    }
}