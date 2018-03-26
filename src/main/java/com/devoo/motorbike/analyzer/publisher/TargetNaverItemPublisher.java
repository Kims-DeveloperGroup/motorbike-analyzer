package com.devoo.motorbike.analyzer.publisher;

import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import com.devoo.motorbike.analyzer.repository.naver.NaverItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.*;

@Repository
@Slf4j
public class TargetNaverItemPublisher {

    private final NaverItemRepository naverItemRepository;
    private final int PAGE_SIZE = 10;
    private final ExecutorService executorService;

    @Autowired
    public TargetNaverItemPublisher(NaverItemRepository naverItemRepository) {
        this.naverItemRepository = naverItemRepository;
        executorService = Executors.newSingleThreadExecutor();
    }

    public BlockingQueue<NaverItem> publishNaverItems() throws InterruptedException {
        BlockingQueue<NaverItem> queue = new LinkedBlockingQueue<>(200);
        executorService.submit(() -> {
            log.debug("Staring consuming naver items.");
                    Pageable pageable = PageRequest.of(0, PAGE_SIZE);
            Page<NaverItem> itemsOfPage = Page.empty();
                    try {
                        while (true) {
                            itemsOfPage = findAllByPagination(pageable);
                            log.debug("Read crawling target item from page {}", pageable.getPageNumber());
                            pageable = pageable.next();
                            putItemsToQueue(queue, itemsOfPage.getContent());
                        }
                    } catch (InterruptedException e) {
                        log.error("Exception was thrown while reading naver items into queue. current page:{}",
                                pageable.getPageNumber());
                        return;
                    }
                }
        );
        return queue;
    }

    private Page<NaverItem> findAllByPagination(Pageable pageable) {
        return naverItemRepository.findAll(pageable);
    }

    private void putItemsToQueue(BlockingQueue<NaverItem> queue, Collection<NaverItem> items) throws InterruptedException {
        for (NaverItem naverItem : items) {
            queue.put(naverItem);
        }
    }

    public void stopPublishing() throws InterruptedException {
        log.debug("Stopping {}", getClass().getName());
        this.executorService.awaitTermination(5, TimeUnit.SECONDS);
    }
}