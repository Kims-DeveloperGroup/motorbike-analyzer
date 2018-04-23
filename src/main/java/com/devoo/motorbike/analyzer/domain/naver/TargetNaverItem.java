package com.devoo.motorbike.analyzer.domain.naver;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "naver-item", type = "naveritem")
public class TargetNaverItem {
    @Id
    private String link;
    private String title;
    private String subTitle;
    private String description;
    private String cafeName;
    private String query;
    private String date;
}