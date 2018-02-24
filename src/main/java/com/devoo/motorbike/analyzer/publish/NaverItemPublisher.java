package com.devoo.motorbike.analyzer.publish;

import com.devoo.motorbike.analyzer.domain.naver.NaverItem;
import com.devoo.motorbike.analyzer.repository.naver.AsyncNaverItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

@Slf4j
public class NaverItemPublisher implements Publisher<NaverItem> {

    private final AsyncNaverItemRepository asyncNaverItemRepository;

    @Autowired
    public NaverItemPublisher(AsyncNaverItemRepository asyncNaverItemRepository) {
        this.asyncNaverItemRepository = asyncNaverItemRepository;
    }

    @Override
    public void subscribe(Subscriber<? super NaverItem> subscriber) {
        Flux<NaverItem> publisher = asyncNaverItemRepository.findAll();
        subscriber.onSubscribe(null);
        publisher.subscribe(subscriber::onNext);
        subscriber.onComplete();
    }
}