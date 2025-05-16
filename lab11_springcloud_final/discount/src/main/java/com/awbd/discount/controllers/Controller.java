package com.awbd.discount.controllers;

import com.awbd.discount.config.PropertiesConfig;
import com.awbd.discount.model.Discount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    private PropertiesConfig configuration;

    @PreAuthorize("hasAuthority('SCOPE_demo')")
    @GetMapping("/discount")
    public Discount getDiscount(){

        return new Discount(configuration.getMonth(),configuration.getYear(), configuration.getVersionId());
    }
}
