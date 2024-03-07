package com.awbd.lab1cs;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("myBooksSubscription")
public class BooksSubscription implements Subscription, ApplicationContextAware {

    private static ApplicationContext applicationContext;
    DiscountCalculator discountCalculator;

    public BooksSubscription() {
    }
    @Autowired
    public BooksSubscription(@Qualifier("discountCalculatorImpl") DiscountCalculator discountCalculator) {

        this.discountCalculator = discountCalculator;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    //@Autowired
    //Invoice invoice;
    public Invoice getInvoice(){
        return applicationContext.getBean(Invoice.class);
    }

    public double getPrice() {
        return discountCalculator.calculate(450);
    }

    public String getDescription() {
        return "books subscription -- yearly payment plan";
    }


}
