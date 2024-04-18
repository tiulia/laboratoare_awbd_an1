package com.awbd.lab8.services;

import com.awbd.lab8.domain.Movie;
import com.awbd.lab8.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    MovieRepository movieRepository;

    @Override
    public Optional<Movie> findById(String id) {

        return movieRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        movieRepository.deleteById(id);
    }

    @Override
    public List<Movie> findAll() {

        return movieRepository.findAll();
    }

    @Override
    public Page<Movie> findPaginated(Pageable pageable) {
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        return moviePage;
    }
}

