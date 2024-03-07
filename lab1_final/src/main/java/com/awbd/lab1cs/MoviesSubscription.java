package com.awbd.lab1cs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("myMoviesSubscription")
public class MoviesSubscription implements Subscription {
    DiscountCalculator discountCalculator;

    Features features;

    @Autowired
    public void setFeatures(Features features){
        this.features = features;
    }

    @Autowired
    @Qualifier("externalCalculator")
    public void setDiscountCalculator(DiscountCalculator discountCalculator) {
        this.discountCalculator = discountCalculator;
    }

    public double getPrice() {
        return discountCalculator.calculate(100);
    }

    public String getDescription() {
        return "movies subscription -- monthly payment plan";
    }

    public void addFeature(String option){
        features.addFeature(option);
    }
}


