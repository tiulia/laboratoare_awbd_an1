package com.awbd.lab9.controllers;

import com.awbd.lab9.domain.Movie;
import com.awbd.lab9.services.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/movie")
public class MovieController {

    MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @RequestMapping("/info/{id}")
    public String showById(@PathVariable String id, Model model){
        Optional<Movie> movieOpt = movieService.findById(id);
        if (movieOpt.isPresent()) {
            model.addAttribute("movie", movieOpt.get());
            return "movieInfo";
        }
        else {
            model.addAttribute("id", id);
            return "notFoundException";
        }
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable String id, Model model){
        Optional<Movie> movieOpt = movieService.findById(id);
        if (movieOpt.isPresent()) {
            movieService.deleteById(id);
            return "redirect:/movie";
        }
        else {
            model.addAttribute("id", id);
            return "notFoundException";
        }
    }

    @RequestMapping({""})
    public String getMoviePage(Model model,
                               @RequestParam("page") Optional<Integer> page,
                               @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Movie> moviePage = movieService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("moviePage",moviePage);

        return "moviePaginated";
    }
}

