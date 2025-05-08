package com.awbd.lab9.services;

import com.awbd.lab9.domain.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> findAll();
    Optional<Movie> findById(String id);
    void deleteById(String id);
    Page<Movie> findPaginated(Pageable pageable);
}
