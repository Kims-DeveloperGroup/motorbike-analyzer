package com.devoo.motorbike.analyzer.domain;

import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonObject;
import lombok.Data;
import org.jsoup.nodes.Document;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@org.springframework.data.elasticsearch.annotations.Document(indexName = "result-item")
public class NaverDocumentWrapper {
    @Id
    private String id;
    private final TargetNaverItem targetNaverItem;
    @JsonIgnore
    private Document document;
    private DocumentStatus status;
    private List<JsonObject> processedResults = new ArrayList<>();

    public NaverDocumentWrapper(Document document, TargetNaverItem targetNaverItem) {
        this.document = document;
        this.targetNaverItem = targetNaverItem;
        this.id = targetNaverItem.getLink();
    }

    public void addProcessedResult(JsonObject processedResult) {
        processedResults.add(processedResult);
    }
}