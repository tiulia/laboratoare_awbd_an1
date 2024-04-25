package com.awbd.lab9.controllers;

import com.awbd.lab9.domain.Movie;
import com.awbd.lab9.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    MovieService movieService;


    @RequestMapping({"", "/", "/index"})
    public String getIndexPage(Model model){
        List<Movie> movies = movieService.findAll();
        model.addAttribute("movies",movies);

        return "movieList";
    }




}
