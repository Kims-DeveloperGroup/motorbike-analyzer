package com.devoo.motorbike.analyzer.repository.naver;

import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

@Repository
@Slf4j
public class AsyncNaverItemRepository {

    private final NaverItemRepository naverItemRepository;
    private final int PAGE_SIZE = 10;

    @Autowired
    public AsyncNaverItemRepository(NaverItemRepository naverItemRepository) {
        this.naverItemRepository = naverItemRepository;
    }

    public Flux<NaverItem> findAll() {
        final Pageable[] pageable = {PageRequest.of(0, PAGE_SIZE)};
        return Flux.fromStream((() -> Stream.generate(() -> {
            log.debug("Reading crawling target item from page {}", pageable[0].getPageNumber());
            List<NaverItem> items = findAllByPagination(pageable[0]);
            pageable[0] = pageable[0].next();
            delayTime(1000L);
            return items;
        }))).flatMap(Flux::fromIterable);
    }

    private List<NaverItem> findAllByPagination(Pageable pageable) {
        return naverItemRepository.findAll(pageable).getContent();
    }

    private void delayTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}