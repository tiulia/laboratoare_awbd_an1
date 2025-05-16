package com.awbd.lab9.services;

import com.awbd.lab9.domain.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface MovieService {

    Flux<Movie> findAll();

    Mono<Movie> findById(String id);

    Mono<Void> deleteById(String id);

    Mono<Page<Movie>> findPaginated(Pageable pageable);
}
