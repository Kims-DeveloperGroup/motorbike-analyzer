package com.devoo.motorbike.analyzer.repository;

import com.devoo.motorbike.analyzer.domain.SaleItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends ElasticsearchRepository<SaleItem, String> {
}