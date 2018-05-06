package com.devoo.motorbike.analyzer.domain;

import com.devoo.motorbike.analyzer.constants.DocumentStatus;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@org.springframework.data.elasticsearch.annotations.Document(indexName = "result-item")
public class NaverDocumentWrapper {
    @Id
    private String id;
    private TargetNaverItem targetNaverItem;
    @JsonIgnore
    private Document document;
    private DocumentStatus status;
    @Field(type = FieldType.Object)
    private List<Object> processedResults = new ArrayList<>();

    public NaverDocumentWrapper(Document document, TargetNaverItem targetNaverItem) {
        this.document = document;
        this.targetNaverItem = targetNaverItem;
        this.id = targetNaverItem.getLink();
    }

    public void addProcessedResult(ProcessResult processedResult) {
        processedResults.add(processedResult);
    }
}