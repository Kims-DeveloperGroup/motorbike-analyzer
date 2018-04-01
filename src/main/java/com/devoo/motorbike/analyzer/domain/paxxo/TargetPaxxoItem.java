package com.devoo.motorbike.analyzer.domain.paxxo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "paxxo-item")
public class TargetPaxxoItem {
    @Id
    private long id;
    private String title;
    private String maker;
    private String model;
    private int releaseYear;
    private String price;
    private String region;
    private String updateDate;
    private int salesStatus;
    private String url;
}
