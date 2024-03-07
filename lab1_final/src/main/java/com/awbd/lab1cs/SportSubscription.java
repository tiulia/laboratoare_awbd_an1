package com.awbd.lab1cs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component("mySportSubscription")
public class SportSubscription implements Subscription {


    @Autowired
    DiscountCalculator discountCalculator;

    @Autowired
    Features features;

    @PreDestroy
    public void customDestroy()
    {
        System.out.println("Bean SportSubscription customDestroy() invoked...");
    }

    public double getPrice() {
        return discountCalculator.calculate(1000);
    }
    public String getDescription() {
        return "sport subscription -- go practice";
    }

    public void addFeature(String option){
        features.addFeature(option);
    }

}
