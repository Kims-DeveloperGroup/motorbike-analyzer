package com.devoo.motorbike.analyzer.repository.paxxo;

import com.devoo.motorbike.analyzer.domain.Model;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaxxoModelRepository extends ElasticsearchRepository<Model, Long> {
}