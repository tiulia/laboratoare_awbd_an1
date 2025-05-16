
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

#### Step 1
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

#### Step 2

Test:

```
http://localhost:8070/subscription/default
http://localhost:8070/discount/prod
http://localhost:8070/discount/dev
http://localhost:8761/

http://localhost:8071/actuator
http://localhost:8071/actuator/gateway/routes get request.

http://localhost:8071/discount/discount get request.
http://localhost:8071/subscription/subscription/1
```

#### Step 3
Create a RouteLocator that will handle request with prefix awbd:
Test the GET request: http://locahost:8071/awbd/subscription/subscription/1

```
@Bean
public RouteLocator myRoutes(RouteLocatorBuilder builder) {
	return builder.routes()
			.route("subscription_route", r -> r
					.path("/awbd/subscription/**")
					.filters(f -> f.rewritePath("/awbd/subscription/(?<segment>.*)", "/${segment}"))
					.uri("lb://SUBSCRIPTION"))

			.route("discount_route", r -> r
					.path("/awbd/discount/**")
					.filters(f -> f.rewritePath("/awbd/discount/(?<segment>.*)", "/${segment}"))
					.uri("lb://DISCOUNT"))
				.build();
}
```

We can configure routs using properties in application.properties:

```
spring.cloud.gateway.routes[0].id=subscription_route
spring.cloud.gateway.routes[0].uri=lb://SUBSCRIPTION
spring.cloud.gateway.routes[0].predicates[0]=Path=/awbd/subscription/**
spring.cloud.gateway.routes[0].filters[0]=RewritePath=/awbd/subscription/(?<segment>.*), /${segment}

spring.cloud.gateway.routes[1].id=discount_route
spring.cloud.gateway.routes[1].uri=lb://DISCOUNT
spring.cloud.gateway.routes[1].predicates[0]=Path=/awbd/discount/**
spring.cloud.gateway.routes[1].filters[0]=RewritePath=/awbd/discount/(?<segment>.*), /${segment}
```

#### Step 4
Disable the property spring.cloud.gateway.discovery.locator.enabled.
If this property is set to false only the paths specified in the RouteLocator 
will be available.

```
spring.cloud.gateway.discovery.locator.enabled=false
```

Test Actuator get request:
```
http://localhost:8071/actuator/gateway/routes.
```

#### Step 5
Add a filter that will send a header inside the response.

```java
@Bean
public RouteLocator myRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
            .route("subscription_route", r -> r
                    .path("/awbd/subscription/**")
                    .filters(f -> f.rewritePath("/awbd/subscription/(?<segment>.*)", "/${segment}")
                            .filter((exchange, chain) -> {
                                long start = System.currentTimeMillis();
                                return chain.filter(exchange).then(
                                        Mono.fromRunnable(() -> {
                                            long duration = System.currentTimeMillis() - start;
                                            exchange.getResponse().getHeaders().add("X-Response-Time", duration + "ms");
                                        })
                                );
                            })
                    )
                    .uri("lb://SUBSCRIPTION"))
            .route("discount_route", r -> r
                    .path("/awbd/discount/**")
                    .filters(f -> f.rewritePath("/awbd/discount/(?<segment>.*)", "/${segment}")
                            .filter((exchange, chain) -> {
                                long start = System.currentTimeMillis();
                                return chain.filter(exchange).then(
                                        Mono.fromRunnable(() -> {
                                            long duration = System.currentTimeMillis() - start;
                                            exchange.getResponse().getHeaders().add("X-Response-Time", duration + "ms");
                                        })
                                );
                            })
                    )
                    .uri("lb://DISCOUNT"))

            .build();
}
```

### Keycloak
Keycloak is an open-source Identity and Access Management (IAM) 
solution. It provides a centralized way to handle authentication, 
authorization, 
and user management for modern applications and services.

- Supports standard security protocols, 
- Assign roles and permissions to users,
- Manage users, clients, roles, and configurations.

**Realm**: A space to manage a set of users, credentials, roles, and clients. Think of it like a tenant or project.

**Client**: An application or service that wants to authenticate via Keycloak.
An **OpenID client** is any app that wants 
to log in users through a trusted provider 
and get their identity in a secure way.

**User**: Represents a person or system accessing the services.

**Role**: Permissions that can be assigned to users or groups.

**Token**: Secure object containing user identity and roles (usually JWT).


#### Step 6

Start a docker container:

https://www.keycloak.org/getting-started/getting-started-docker

Configure a Realm, a Client and a User.

```
docker run -d --name keycloak -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:23.0.7 start-dev
```

#### Step 7
Add maven dependencies.

Gateway:

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-oauth2-resource-server</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-oauth2-jose</artifactId>
</dependency>
```


subscription and discount:

```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-oauth2-resource-server</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-oauth2-jose</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-webflux</artifactId>
</dependency>
```

Subscription:
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

#### Step 9
Add SecurityConfig classes:

Gateway:

```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(auth -> auth.anyExchange().authenticated())
                .oauth2Login(withDefaults())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }

}
```

Discount and Subscription:

```
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
```

```
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/webjars/**"
                ).permitAll().anyRequest().authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
```

#### Step 10

Add @PreAuthorize annotation in Controllers.

```
@PreAuthorize("hasAuthority('SCOPE_demo')")
```

#### Step 11

**WebClient** is a non-blocking, 
reactive HTTP client introduced in Spring 5 
as part of the WebFlux framework. It's designed to perform asynchronous HTTP requests and efficiently handle large numbers of concurrent connections.

Modify DiscountServiceProxy in Subscription:

```
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
```

#### Step 12

Modify getSubscription request:


```java
@PreAuthorize("hasAuthority('SCOPE_demo')")
@GetMapping("/subscription/{subscriptionId}")
@CircuitBreaker(name="discountById", fallbackMethod = "getSubscriptionFallback")
public Subscription getSubscription(@PathVariable Long subscriptionId) {

    Subscription subscription = subscriptionService.findById(subscriptionId);


    discountServiceProxy.findDiscount()
            .doOnNext(discount -> logger.info(discount.getVersionId()))
            .map(discount -> {
                subscription.setPrice(subscription.getPrice() * (100 - discount.getMonth()) / 100);
                return subscription;
            }).block();


    return subscription;

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


#### Step 13 

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


#### Step 14

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

#### Step 15
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

#### Step 16

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
