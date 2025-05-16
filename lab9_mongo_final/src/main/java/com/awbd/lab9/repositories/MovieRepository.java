package com.awbd.lab9.repositories;

import com.awbd.lab9.domain.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface MovieRepository extends ReactiveMongoRepository<Movie, String> {
    Flux<Movie> findByTitle(String title);


    Flux<Movie> findByYearBetween(int start, int end);

    @Query("{ 'year' : { $gt: ?0, $lt: ?1 } }")
    Flux<Movie> findByYearBetweenQ(int start, int end);


    @Query("{ 'title' : { $regex: ?0 } }")
    Flux<Movie> findByTitleRegexp(String regexp);

    Mono<Movie> findById(String title);

    Flux<Movie> findByTitleIsNotNull(Pageable pageable);
}