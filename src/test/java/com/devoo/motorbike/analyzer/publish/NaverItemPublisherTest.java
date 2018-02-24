package com.devoo.motorbike.analyzer.publish;

import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import com.devoo.motorbike.analyzer.repository.naver.AsyncNaverItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(MockitoJUnitRunner.class)
public class NaverItemPublisherTest {
    @InjectMocks
    private NaverItemPublisher naverItemPublisher;

    @Mock
    private AsyncNaverItemRepository asyncNaverItemRepository;

    @Test
    public void shouldBeNaverItemsPublished_whenStringSubscription() {
        //Given
        NaverItem[] items = new NaverItem[]{new NaverItem(), new NaverItem()};
        Mockito.when(asyncNaverItemRepository.findAll()).thenReturn(Flux.fromArray(items));

        //When
        naverItemPublisher.subscribe(new MockSubscriber());

        //Then
        StepVerifier.create(naverItemPublisher).expectNextCount(2).verifyComplete();
    }

    private class MockSubscriber implements Subscriber<NaverItem> {
        @Override
        public void onSubscribe(Subscription s) {
            System.out.println("on Subscribe");
        }

        @Override
        public void onNext(NaverItem naverItem) {
            System.out.println("onNext");
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onComplete() {
            System.out.println("on Complete");
        }
    }
}