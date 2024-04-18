package com.awbd.lab8.controllers;

import com.awbd.lab8.domain.Movie;
import com.awbd.lab8.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    MovieService movieService;

    @RequestMapping("/info/{id}")
    public String showById(@PathVariable String id, Model model){
        Optional<Movie> movieOpt = movieService.findById(id);
        if (movieOpt.isPresent()) {
            model.addAttribute("movie", movieOpt.get());
            return "movieinfo";
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
            return "redirect:/movies";
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

