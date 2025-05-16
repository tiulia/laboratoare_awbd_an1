package com.awbd.lab9.controllers;

import com.awbd.lab9.domain.Movie;
import com.awbd.lab9.services.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Controller
@RequestMapping("/movie")
public class MovieController {


    MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @RequestMapping("/info/{id}")
    public Mono<Rendering> showById(@PathVariable String id){

        Mono<Movie> movie = movieService.findById(id);
        return Mono.just(Rendering.view("movieInfo")
                .modelAttribute("movie", movie).build());
    }


    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable String id){
        movieService.deleteById(id).block();
        return "redirect:/index";
    }

    @RequestMapping({""})
    public Mono<Rendering> getMoviePage(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Mono<Page<Movie>> moviePage = movieService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        return Mono.just(Rendering.view("moviePaginated")
                .modelAttribute("moviePage", moviePage).build());

    }
}