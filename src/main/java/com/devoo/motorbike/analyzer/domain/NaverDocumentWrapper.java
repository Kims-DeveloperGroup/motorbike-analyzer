package com.devoo.motorbike.analyzer.domain;

import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import lombok.Data;
import org.jsoup.nodes.Document;

@Data
public class NaverDocumentWrapper {
    private final TargetNaverItem targetNaverItem;
    private final Document document;
    private DocumentStatus status;

    public NaverDocumentWrapper(Document document, TargetNaverItem targetNaverItem) {
        this.document = document;
        this.targetNaverItem = targetNaverItem;
    }
}