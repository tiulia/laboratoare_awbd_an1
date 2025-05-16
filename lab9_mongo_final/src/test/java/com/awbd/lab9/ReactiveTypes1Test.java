package com.awbd.lab9;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class ReactiveTypes1Test {

    @Test
    public void Subscriber(){
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4).log()
                .subscribe(elements::add);

        assertThat(elements).containsExactly(1, 2, 3, 4);
    }


    @Test
    public void BackPressure(){
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4).log()
                .subscribe(new CoreSubscriber<Integer>() {
                    private Subscription s;
                    int onNextAmount;

                    @Override
                    public void onSubscribe(Subscription s) {
                        this.s = s;
                        s.request(2);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        elements.add(integer);
                        onNextAmount++;
                        if (onNextAmount % 2 == 0) {
                            s.request(2);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {}

                    @Override
                    public void onComplete() {}
                });
        assertThat(elements).containsExactly(1, 2, 3, 4);
    }

    @Test
    public void FluxMap(){
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4).log()
                .map(i -> i * 2)
                .subscribe(elements::add);

        assertThat(elements).containsExactly(2, 4, 6, 8);
    }
}