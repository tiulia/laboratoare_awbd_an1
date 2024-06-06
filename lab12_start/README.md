
# Project 12

- Spring Cloud Gateway

### Edge server/Gateway advantages

- External client communicate with a single entry point. 
Gateway will froward request to microservices. 
Clients store only the address of the gateway.   
- DRY for cross-cutting concerns => tracing, authorization, logging etc.
- prevent cascading failures => fault tolerance and resilience.
- dynamic routing, for instance routing based on API version.

#### Spring Cloud Gateway
- based on Spring Reactive Framework.
- can handle pre-filters and post-filters.

- In Spring Initiliz we should add dependencies:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

#### Exercise 1
Configure properties in application.properties 

```
spring.application.name=gatewayserver 
spring.config.import=optional:configserver:http://localhost:8070/

#actuator
info.app.name=Gateway Server
info.app.description=Gateway Server Application
info.app.version=1.0.0
management.info.env.enabled = true
management.endpoint.gateway.enabled=true
management.endpoints.web.exposure.include=*
management.endpoint.shutdown.enabled=true

#eureka
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.discovery.locator.lowerCaseServiceId=true
```

Configure eureka related properties in the file gatewayserver.properties 
saved on the config server.

```
server.port = 8071

eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

#### Exercise 2

Test http://localhost:8071/actuator/gateway/routes get request.
Test http://192.168.0.159:8071/discount/discount get request.
Test http://192.168.0.159:8071/subscription/subscription/1

#### Exercise 3
Create a RouteLocator that will handle request with prefix awbd:
Test the GET request: http://192.168.0.159:8071/awbd/subscription/subscription/1

```
@Bean
public RouteLocator myRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
            .route(p -> p
                    .path("/awbd/subscription/**")
                    .filters(f -> f.rewritePath("/awbd/subscription/(?<segment>.*)","/${segment}")                           
                    .uri("lb://SUBSCRIPTION")) //ln load balancer + application_name
            .route(p -> p
                    .path("/awbd/discount/**")
                    .filters(f -> f.rewritePath("/awbd/discount/(?<segment>.*)","/${segment}")
                    .uri("lb://DISCOUNT")).build();
}
```

#### Exercise 4
Disable the property spring.cloud.gateway.discovery.locator.enabled.
If this property is set to false only the paths specified in the RouteLocator 
will be available.

```
spring.cloud.gateway.discovery.locator.enabled=false
```

Test Actuator get request:
http://localhost:8071/actuator/gateway/routes.


#### Exercise 5
Add a filter that will send a header inside the response.

```java
@Bean
public RouteLocator myRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
            .route(p -> p
                    .path("/awbd/subscription/**")
                    .filters(f -> f.rewritePath("/awbd/subscription/(?<segment>.*)","/${segment}")
                            .addResponseHeader("X-Response-Time",new Date().toString()))
                    .uri("lb://SUBSCRIPTION")) //ln load balancer + application_name
            .route(p -> p
                    .path("/awbd/discount/**")
                    .filters(f -> f.rewritePath("/awbd/discount/(?<segment>.*)","/${segment}")
                            .addResponseHeader("X-Response-Time",new Date().toString()))
                    .uri("lb://DISCOUNT")).build();
}
```

#### Cross-cutting concern correlation-id.

A correlation ID is a unique identifier that is used 
to trace and log requests performed by different microservices.
This ID allows tracking a single request 
as it triggers other request through various services, 
making it easier to debug and monitor performance. 

When a request enters the system (e.g., from an external client), 
a unique correlation ID is generated. 
This ID can be a simple UUID or any other unique string. This 
correlation ID is passed along with the request to all subsequent 
microservices involved in handling the request. 
The ID can be included in HTTP headers. Each service logs relevant information about the request, including the correlation ID. 


#### Exercise 6

Add a filter that will add a correlation ID in each request.

```java
@Order(1)
@Component
@Slf4j
public class LogTraceFilter implements GlobalFilter {

    public static final String CORRELATION_ID = "awbd-id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        log.info("awbd request ... ");

        if (this.getCorrelationId(requestHeaders) != null) {
            log.info("correlation-id found {}:",
                    this.getCorrelationId(requestHeaders));
        } else {
            String correlationID = generateCorrelationId();
            exchange = this.setCorrelationId(exchange, correlationID);
            log.info("correlation-id generated: {}", correlationID);
        }
        return chain.filter(exchange);
    }



    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

    private String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) != null) {
            return requestHeaders.get(CORRELATION_ID).stream().findFirst().get();
        } else {
            return null;
        }
    }


    private ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return exchange.mutate().request(exchange.getRequest().mutate().header(CORRELATION_ID, correlationId).build()).build();

    }


}
```


#### Exercise 6

Add a filter that will add a correlation ID in each response.

```java
@Configuration
@Slf4j
public class ResponseFilter {


    public static final String CORRELATION_ID = "awbd-id";
    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                String correlationId = null;
                if (requestHeaders.get(CORRELATION_ID) != null)
                    correlationId = requestHeaders.get(CORRELATION_ID).stream().findFirst().get();

                log.info("Updated the correlation id to response headers: {}", correlationId);
                exchange.getResponse().getHeaders().add(CORRELATION_ID, correlationId);
            }
                )
            );
        };
    }
}
```

#### Exercise 7
Modify Subscription and Discount services so that requests should log the correlation-Id

In Discount and Subscription service add @RequestHeader("awbd-id"):

```java
@GetMapping("/discount")
public ResponseEntity<Discount> getDiscount(@RequestHeader("awbd-id")
                                                    String correlationId){
     Discount discount = new Discount(configuration.getMonth(),configuration.getYear(), configuration.getVersionId());
     log.info("correlation-id discount: {}", correlationId);
     return ResponseEntity.status(HttpStatus.SC_OK).body(discount);
}
```

```java
@FeignClient(value = "discount")
public interface DiscountServiceProxy {
    @GetMapping("/discount")
    ResponseEntity<Discount> findDiscount(@RequestHeader("awbd-id")
                          String correlationId);
}
```

```java
@GetMapping("/subscription/coach/{coach}/sport/{sport}")
public ResponseEntity<Subscription> findByCoachAndSport(@RequestHeader("awbd-id") String correlationId, @PathVariable String coach,
                                     @PathVariable String sport){
    Subscription subscription = subscriptionService.findByCoachAndSport(coach, sport);

    Link selfLink = linkTo(methodOn(SubscriptionController.class).getSubscription(subscription.getId())).withSelfRel();
    subscription.add(selfLink);

    ResponseEntity<Discount> responseDiscount = discountServiceProxy.findDiscount(correlationId);
    Discount discount = responseDiscount.getBody();
    logger.info(discount.getVersionId());
    logger.info("correlation-id subscription: {}", correlationId);
    subscription.setPrice(subscription.getPrice() * (100 - discount.getMonth())/100);

    return ResponseEntity.status(HttpStatus.SC_OK).body(subscription);
}
```

#### Exercise 8

Start Zipkin server and test 

```
set RABBIT_URI=amqp://localhost
java -jar zipkin-server-3.4.0-exec.jar
```

Add dependencies:
```
<dependency>
	<groupId>io.opentelemetry</groupId>
	<artifactId>opentelemetry-exporter-zipkin</artifactId>
</dependency>

<dependency>
	<groupId>io.micrometer</groupId>
	<artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>

<dependency>
	<groupId>io.micrometer</groupId>
	<artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```



Set tracing properties in application.properties 
```
management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
logging.pattern.level=%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]
```

### Docs

[1] https://spring.io/projects/spring-cloud-gateway

[2] https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/fluent-java-routes-api.html

[3] https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/gatewayfilter-factories/addresponseheader-factory.html

[4] https://www.rabbitmq.com/docs/install-windows

[5] https://www.erlang.org/patches/otp-25.3.2

[6] https://zipkin.io/pages/quickstart.html
