package com.awbd.lab1cs;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

class ExternalCalculator implements DiscountCalculator{
    public double calculate(int price) {
        return 0.65 * price;
    }
}
class FeaturesImpl implements Features, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Bean Features afterPropertiesSet() invoked... ");
    }

    @PostConstruct
    public void customInit()
    {
        System.out.println("Bean Features customInit() invoked...");
    }

    private List<String> features;
    public FeaturesImpl(){
        features = new ArrayList<>();
    }
    public void addFeature(String feature) {
        features.add(feature);
    }
    @Override
    public String toString() {
        return "FeaturesImpl{" +
                "features=" + features +
                '}';
    }




}

class Invoice{
    String details;

    public Invoice(String details){
        this.details = details;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "details='" + details + '\'' +
                '}';
    }
}


@Configuration
@ComponentScan("com.awbd.lab1cs")
@PropertySource("classpath:application.properties")
public class SubscriptionConfig {

    @Bean
    public DiscountCalculator externalCalculator(){
        return new ExternalCalculator();
    }

    @Bean
    @Scope("prototype")
    public Features featureBean(){ return new FeaturesImpl();}

    @Bean
    @Scope("prototype")
    public Invoice invoice() {return new Invoice(String.valueOf(LocalTime.now()));}

}
