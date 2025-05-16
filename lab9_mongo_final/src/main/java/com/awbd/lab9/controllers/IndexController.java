package com.awbd.lab9.controllers;


import com.awbd.lab9.services.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring6.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;


@Slf4j
@Controller
@RequestMapping("/")
public class IndexController {

    private final MovieService movieService;

    public IndexController(MovieService movieService) {
        this.movieService = movieService;
    }

    @RequestMapping({"", "/", "/index"})
    public String getIndexPage(Model model){

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(movieService.findAll(), 10);

        model.addAttribute("movies", reactiveDataDrivenMode);
        return "movieList";
    }
}
