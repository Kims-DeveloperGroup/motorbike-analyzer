package com.devoo.motorbike.analyzer.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "sale-item")
@Data
public class SaleItem {
    @Id
    private String id;
    private String title;
    private String model;
    private String maker;
    private String price;
    private String region;
    private String onlineShop;
    private int releaseYear;
    private String url;
}