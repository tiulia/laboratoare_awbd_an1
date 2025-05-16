package com.awbd.lab9.services;

import com.awbd.lab9.domain.Movie;
import com.awbd.lab9.repositories.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class MovieServiceImpl implements MovieService {

    MovieRepository reactiveMovieRepository;

    public MovieServiceImpl(MovieRepository reactiveMovieRepository) {
        this.reactiveMovieRepository = reactiveMovieRepository;
    }

    @Override
    public Mono<Movie> findById(String id) {

        return reactiveMovieRepository.findById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        reactiveMovieRepository.deleteById(id).subscribe();
        return Mono.empty();
    }

    @Override
    public Flux<Movie> findAll() {

        return reactiveMovieRepository.findAll();
    }

    @Override
    public Mono<Page<Movie>> findPaginated(Pageable pageable) {
        return
                this.reactiveMovieRepository.findByTitleIsNotNull(pageable)
                .collectList()
                .zipWith(this.reactiveMovieRepository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

}
