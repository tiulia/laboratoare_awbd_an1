package com.awbd.lab9;

import com.awbd.lab9.domain.Movie;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ReactiveTypes2Test {
    @Test
    public void MonoFilter() throws Exception{
        Movie movie = new Movie();
        movie.setTitle("test movie");

        Mono<Movie> movieMono = Mono.just(movie);

        Movie movie2 = movieMono.log().
                filter(m ->m.getTitle().equalsIgnoreCase("test")).block();

        Assertions.assertThatNullPointerException()
                .isThrownBy(() -> log.info(movie2.toString()));

        Movie movie1 = movieMono.log().
                filter(m ->m.getTitle().equalsIgnoreCase("test movie")).block();
        log.info(movie1.toString());

    }

    @Test
    public void FluxDelay() throws Exception{
        Flux<String> fluxString = Flux.just("one","two","three");

        CountDownLatch countDownLatch = new CountDownLatch(1);

        fluxString.delayElements(Duration.ofSeconds(5)).log()
                .doOnComplete(countDownLatch::countDown)
                .subscribe(log::info);

        countDownLatch.await();
    }
}