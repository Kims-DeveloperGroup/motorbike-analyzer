package com.devoo.motorbike.analyzer.repository.paxxo;


import com.devoo.motorbike.analyzer.domain.paxxo.TargetPaxxoItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaxxoItemRepository extends ElasticsearchRepository<TargetPaxxoItem, Long> {
}