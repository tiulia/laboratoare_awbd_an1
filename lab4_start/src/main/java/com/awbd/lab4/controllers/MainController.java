package com.awbd.lab4.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
    @RequestMapping({"","/","/home"})
    public ModelAndView getHome(){

        return new ModelAndView("main");
    }

}
