package com.awbd.lab9.repositories;

import com.awbd.lab9.domain.Movie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataMongoTest
@Slf4j
public class MovieRepositoryQueryTest {

    @Autowired
    MovieRepository movieRepository;

    @Test
    public void findByYearBetween() {
        List<Movie> movies = movieRepository.findByYearBetween(1960,1970);
        assertFalse(movies.isEmpty());
        log.info("findByYearBetween ...");
        movies.forEach(movie -> log.info(movie.toString()));
    }



    @Test
    public void findMoviesByRegexpTitle() {
        List<Movie> movies = movieRepository.findByTitleRegexp("^A");
        assertFalse(movies.isEmpty());
        log.info("findByRegexpTitle ...");
        movies.forEach(movie -> log.info(movie.toString()));
    }

}
