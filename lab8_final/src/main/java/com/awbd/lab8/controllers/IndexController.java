package com.awbd.lab8.controllers;

import com.awbd.lab8.domain.Movie;
import com.awbd.lab8.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class IndexController {

    @Autowired
    MovieService movieService;


    @RequestMapping({"", "/", "/index"})
    public String getIndexPage(Model model){
        List<Movie> movies = movieService.findAll();
        model.addAttribute("movies",movies);
        System.out.println(movies.size());

        return "movieList";
    }




}
