package com.awbd.subscription.services;

import com.awbd.subscription.model.Discount;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DiscountServiceProxy {

    private final WebClient webClient;

    public DiscountServiceProxy(WebClient.Builder builder) {
        this.webClient = builder.filter(new ServletBearerExchangeFilterFunction()).build(); // uses load-balanced builder
    }

    public Mono<Discount> findDiscount() {
        return webClient
                .get()
                .uri("lb://DISCOUNT/discount") // ✅ Eureka service name
                .header(HttpHeaders.AUTHORIZATION)
                .retrieve()
                .bodyToMono(Discount.class);
    }
}
