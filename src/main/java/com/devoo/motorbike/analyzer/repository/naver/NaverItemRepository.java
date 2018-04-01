package com.devoo.motorbike.analyzer.repository.naver;

import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NaverItemRepository extends ElasticsearchRepository<TargetNaverItem, String> {
}
