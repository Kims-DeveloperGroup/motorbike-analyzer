package com.devoo.motorbike.analyzer.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "paxxo-model", type = "paxxomodel")
@Data
public class Model {
    @Id
    private Long id;
    private String name;
}