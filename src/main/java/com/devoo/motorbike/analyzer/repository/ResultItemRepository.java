package com.devoo.motorbike.analyzer.repository;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultItemRepository extends ElasticsearchRepository<NaverDocumentWrapper, String> {
}