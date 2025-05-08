package com.awbd.lab9.controllers;

import com.awbd.lab9.domain.Movie;
import com.awbd.lab9.services.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
public class IndexController {

    MovieService movieService;

    public IndexController(MovieService movieService) {
        this.movieService = movieService;
    }

    @RequestMapping({"", "/", "/index"})
    public String getIndexPage(Model model){
        List<Movie> movies = movieService.findAll();
        model.addAttribute("movies",movies);
        log.info("Movies: " + movies.size());

        return "movieList";
    }




}
