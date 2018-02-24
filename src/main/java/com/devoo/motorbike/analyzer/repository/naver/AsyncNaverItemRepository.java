package com.devoo.motorbike.analyzer.repository.naver;

import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

@Repository
public class AsyncNaverItemRepository {

    private final NaverItemRepository naverItemRepository;
    private final int PAGE_SIZE = 10;

    @Autowired
    public AsyncNaverItemRepository(NaverItemRepository naverItemRepository) {
        this.naverItemRepository = naverItemRepository;
    }

    public Flux<NaverItem> findAll() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        return Flux.fromStream((() -> Stream.generate(() -> {
            List<NaverItem> items = findAllByPagination(pageable);
            pageable.next();
            return items;
        }))).flatMap(Flux::fromIterable);
    }

    private List<NaverItem> findAllByPagination(Pageable pageable) {
        return naverItemRepository.findAll(pageable).getContent();
    }
}