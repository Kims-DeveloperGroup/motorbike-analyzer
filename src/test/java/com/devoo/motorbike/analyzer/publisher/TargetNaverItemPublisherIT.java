package com.devoo.motorbike.analyzer.publisher;

import com.devoo.motorbike.analyzer.config.ElasticSearchConfig;
import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import com.devoo.motorbike.analyzer.repository.naver.NaverItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TargetNaverItemPublisher.class, ElasticSearchConfig.class, NaverItemRepository.class})
public class TargetNaverItemPublisherIT {
    private static Logger log = LoggerFactory.getLogger(TargetNaverItemPublisherIT.class);
    @Autowired
    private TargetNaverItemPublisher targetNaverItemPublisher;

    @Autowired
    private NaverItemRepository naverItemRepository;

    @Test
    public void shouldBeNaverItemsBeingEnqueuedMoreOrEqualToExpectedCount_whenConsumingItems() throws InterruptedException {
        //Given
        long totalItemCount = naverItemRepository.count();
        long expectedConsumingCount = totalItemCount > 100 ? 100 : totalItemCount;
        //When
        BlockingQueue<NaverItem> itemQueue = targetNaverItemPublisher.publishNaverItems();

        //Then
        long actualConsumingCount = countConsumingItems(itemQueue, expectedConsumingCount);

        Assertions.assertThat(actualConsumingCount).isEqualTo(expectedConsumingCount);
    }

    private long countConsumingItems(BlockingQueue<NaverItem> itemQueue, long consumingLimit) throws InterruptedException {
        NaverItem consumed;
        AtomicLong count = new AtomicLong(0L);
        do {
            consumed = itemQueue.poll(1L, TimeUnit.SECONDS);
            log.debug("consumed naver item: {}", consumed.getLink());
        } while (consumed != null && count.incrementAndGet() < consumingLimit);
        return count.get();
    }

    @After
    public void terminateConsuming() throws InterruptedException {
        targetNaverItemPublisher.stopPublishing();
    }

}