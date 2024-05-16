# Project 10

- RESTFUL Webservices, 
- Spring Cloud - Config Servers.

### RESTFUL Webservices

-   Architectural design pattern [1].  (Reactive systems)
-   Reactive programming: event-based, asynchronous, non-blocking programming technique.
-	Lightweight, Message body in JSON/XML/ HTML/…  format.
-	Client – server communication, stateless. 

![img.png](img.png)

REST architectural constraints.

**UNIFORM INTERFACE**:

-	Each resource has unique URI.
-	Uniform requests and representations: naming conventions, link or data format (XML/JSON).
-	All resources should be accessible through a common approach (example HTTP).
-	A resource should not be too large, when needed it may contain links to related information, HATEOAS (Hypermedia as the Engine of Application State).

**CLIENT SERVER SEPARATION**

-	Client and server run independently.
-	Clients and servers may be replaced independently.
-	The only interaction between client and server is through requests and responses.
-	The client should only know the resource URI.

**STATELESS**

-	No server-side sessions.
-	The server will not store any history of requests and will treat each request as new.
-	Each request contains all the information needed.
-	The client is managing the state of the application.

**CACHEABLE RESOURCES**

-	If possible, resources should be cached.
-	Caching improves performance.
-	Cacheable resources should be marked as cacheable and have a version number.
-	Requesting the same resource more than once should be avoided.

**LAYERED SYSTEM**

-	Several layers of servers might interact between the server that returns the response and the client.
-	Example: API on server A, data on server B, authentication on server C.
-	Intermediary servers not visible to the client.

**CODE ON DEMAND (optional)**

-	The response may contain executable code (e.g. JavaScript).

**REST verbs**
**GET**
-	read resource.
-	does not change the resource (read only).
-	Using get multiple times for the same resource won’t change the result (idempotence).
-	Safe operation: does not change state of the resource.

**PUT**
-	Insert if resource not found.
-	Update if resource found.
-	Using PUT multiple times with the same resource won’t change the result (idempotence).
-	Not safe operation: does change state of the resource.

**POST**
-	Creates the resource.
-	Not safe operation: does change state of the resource
-	Using POST multiple times will create multiple resources (not idempotent).

**DELETE**
-	Delete a resource.
-	Not safe operation: does change state of the resource

Optional **REST** verbs: 
-	**HEAD** -- Used to retrieve the same headers as that of GET response. No response body, only headers
-	**OPTIONS** -- List of HTTP methods supported by a resource.
-	**TRACE** -- Debugging, echo header.


### Project setup
The project was generated with Spring initializr with 
dependencies: Spring Data JPA, Spring WEB, 
Config Client, H2 Database, Spring Cloud Discovery, 
Lombok, Spring HATEOAS.

Test the following requests in POSTMAN:

```
GET http://localhost:8080/subscription/5
GET http://localhost:8080/subscription/4
GET http://localhost:8080/subscription/coach/James/sport/tennis
GET http://localhost:8080/subscription/list


POST http://localhost:8080/subscription
{
    "coach": "Ken",
    "sport": "tennis",
    "price": 200
}
```

#### Exercise 1 - Swagger configuration
Add Maven dependency:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

Add package com.awbd.subscriptions.config  
and a class to configure swagger, 
com.awbd.subscriptions.config.SwaggerConfig:

```java
package com.awbd.subscription.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Spring Cloud application API")
                .version("1").description("demo Spring Cloud"));

    }
}
```

Test requests:

http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs

#### Exercise 2
Add documentation delete request from SubscriptionController. [4][5]

```java
@Operation(summary = "delete subscription by id")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "subscription deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Subscription.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid id",
                    content = @Content),
        @ApiResponse(responseCode = "404", description = "Subscription not found",
                    content = @Content)})
@DeleteMapping("/subscription/{subscriptionId}")
public void deleteSubscription(@PathVariable Long subscriptionId){
    
    Subscription subscription = subscriptionService.delete(subscriptionId);
}
```

#### Exercise 3
**Content negotiation.** Accepted types of responses: application/json, application/xml. 
Response type must be specified as a request header.

Add a configuration for content negotiontion:


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON)
                .ignoreAcceptHeader(false) // Consider Accept header
                .favorPathExtension(false) // Do not use file extension
                .mediaType("json", MediaType.APPLICATION_JSON) // Add JSON media type
                .mediaType("xml", MediaType.APPLICATION_XML); // Add XML media type
    }
}
```

Test the request with Accept heaer
```
GET http://localhost:8080/subscription/list
Accept:application/xml
```

### Spring-HATEOAS 
**Hypertext as the Engine of Application State** [6][7][8]

HATEOAS, or Hypermedia as the Engine of Application State, 
empowers APIs to provide clients with navigational 
guidance by embedding links to relevant 
resources and actions within each response. 
By including links to the next potential steps, 
HATEOAS enables clients to dynamically discover 
and interact with the application's capabilities 
without prior knowledge, 
fostering a self-descriptive 
and navigable API architecture.

HATEOAS in Spring MVC generates links pointing 
to controller methods, rendered in supported 
hypermedia formats like HAL 
(Hypertext Application Language). 
HAL is a standardized convention 
for defining hypermedia, embedding links to external 
resources within JSON or XML responses. 
By employing HAL, 
Spring MVC APIs enhance their self-descriptiveness, 
enabling clients to seamlessly navigate and interact 
with the API's resources and actions.[9]

#### Exercise 4
Add @EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL) in class com.awbd.subscription. 
SubscriptionApplication

```java
@SpringBootApplication
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class SubscriptionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriptionApplication.class, args);
	}

}
```

#### Exercise 5
Class subscription should extend RepresentationModel<Subscription>.
RepresentationModel serves as a container for aggregating 
Links within an API. 
By populating this container with relevant links, 
APIs enrich the model with navigational information. 
This enables clients to traverse the API's resources and actions 
effectively, fostering a self-descriptive and discoverable 
API architecture.

```java
public class Subscription extends RepresentationModel<Subscription> {
//...
}
```

#### Exercise 6
Add a link to the resource in the response
request findByCoachAndSport in controller SubscriptionController:

```
@GetMapping("/subscription/coach/{coach}/sport/{sport}")
Subscription findByCoachAndSport(@PathVariable String coach,
                                     @PathVariable String sport){
    Subscription subscription = subscriptionService.findByCoachAndSport(coach, sport);

    Link selfLink = linkTo(methodOn(SubscriptionController.class).getSubscription(subscription.getId())).withSelfRel();
    subscription.add(selfLink);

    return subscription;
}
```

Test request: 
```
GET http://localhost:8080/subscription/coach/James/sport/tennis
```

#### Exercise 7
Add links to the list of Subscriptions in the response body of
findSubscriptions request in SubscriptionController:

```java
@GetMapping(value= "/subscription/list")
public CollectionModel<Subscription> findAll() {

    List<Subscription> subscriptions = subscriptionService.findAll();
    for (final Subscription subscription : subscriptions) {
        Link selfLink = linkTo(methodOn(SubscriptionController.class).getSubscription(subscription.getId())).withSelfRel();
        subscription.add(selfLink);

        Link deleteLink = linkTo(methodOn(SubscriptionController.class).deleteSubscription(subscription.getId())).withRel("deleteSubscription");
        subscription.add(deleteLink);

        Link postLink = linkTo(methodOn(SubscriptionController.class).saveSubscription(subscription)).withRel("saveSubscription");
        subscription.add(postLink);

        Link putLink = linkTo(methodOn(SubscriptionController.class).updateSubscription(subscription)).withRel("updateSubscription");
        subscription.add(putLink);
    }

    Link link = linkTo(methodOn(SubscriptionController.class).findAll()).withSelfRel();
    CollectionModel<Subscription> result = CollectionModel.of(subscriptions, link);
    return result;
}
```

### Spring Cloud Architecture 

Spring Cloud tools for developing common patterns in distributed systems[3]:   

-	**Configuration management** -- Spring Cloud Config Server
-	**Service discovery** -- Naming Server Eureka
-	**Intelligent routing**, load balancing – Ribbon
-	**Visibility**: monitoring services/servers Zipkin Distributed Tracing Server.
-	**Fault tolerance**: Hystrix defaul behaviour in case of failure.

![img_1.png](img_1.png)

#### Exercise 8
Use Spring initializr to create a new project with Artifact Id: config-server. 
Add dependencies: Config Server (Spring Cloud Config).

#### Exercise 9
Annotate class com.awbd.ConfigServerApplication 
with @EnableConfigServer annotation.

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

}
```

#### Exercise 10
Run config-server on port 8070, 
in application.properties add:

```
spring.application.name=config-server
server.port=8070
spring.profiles.active=native
spring.cloud.config.server.native.search-locations=classpath:/config
```

#### Exercise 11
Add in src\main\resources\config files discount.properties, 
discount-dev.properties, discount-prod properties.

```
percentage.month=10
percentage.year=20
```

```
percentage.month=30
percentage.year=40
```

```
percentage.month=50
percentage.year=60
```

#### Exercise 12
Test requests: 

```
GET http://localhost:8070/discount/default
GET http://localhost:8070/discount/prod
GET http://localhost:8070/discount/dev
```

#### Exercise 13
Create a new project with Artifact Id: discount. 
Add dependencies: 
Lombok, Web and Config Client (Spring Cloud Config).

#### Exercise 14
Modify application.properties:

```
spring.application.name=discount
percentage.month=5
percentage.year=7
```

#### Exercise 15
Create package config and class PropertiesConfig.

```java
@Component
@ConfigurationProperties("percentage")
@Getter
@Setter
public class PropertiesConfig {
    private int month;
    private int year;
}

```

#### Exercise 16
Create a package model and a class Discount.

```java
@Setter
@Getter
@AllArgsConstructor
public class Discount {
private int month;
private int year;
}
```

#### Exercise 17
Create a package controllers and class Controller.

```java
@RestController
public class Controller {
    @Autowired
    private PropertiesConfig configuration;

    @GetMapping("/discount")
    public Discount getDiscount(){
        return new Discount(configuration.getMonth(),configuration.getYear());
    }
}
```

#### Exercise 18
Modify file application.properties in project discount:

```
spring.application.name=discount

spring.config.import=optional:configserver:http://localhost:8070/
spring.profiles.active=prod
server.port = 8081
```

Run project discount-service on port 8081.

#### Exercise 19
Modify config-server so that config files are read from github:

```
spring.application.name=config-server
server.port=8070
spring.profiles.active=git

spring.cloud.config.server.git.uri=https://github.com/tiulia/config_files.git
spring.cloud.config.server.git.clone-on-start=true
spring.cloud.config.server.git.default-label=main
```

Test discount-service configuration:

http://localhost:8070/discount/default

#### Exercise 20
Add dependency for Actuator in discount application:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Add annotation @RefreshScope

```java
@SpringBootApplication
@RefreshScope
public class DiscountApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscountApplication.class, args);
	}

}
```

#### Exercise 21

Expose actuator endpoints. In application.properties add:
```
management.endpoints.web.exposure.include=*
```


Modify discount-dev.properties on Git 
and reload configuration for discount service with actuator request:

```
http://localhost:8081/actuator/refresh
```

### Docs

[1] https://restfulapi.net/

[2]  RESTful Web Services,  Leonard Richardson,   Sam Ruby 2007

[3] https://spring.io/projects/spring-cloud

[4] https://swagger.io/resources/articles/documenting-apis-with-swagger/

[5] https://www.baeldung.com/spring-rest-openapi-documentation

[6] https://www.baeldung.com/spring-hateoas-tutorial

[7] https://spring.io/projects/spring-hateoas

[8] https://howtodoinjava.com/spring5/hateoas/spring-hateoas-tutorial/

[9] https://en.wikipedia.org/wiki/Hypertext_Application_Language

[10] https://spring.io/projects/spring-cloud-config

[11] https://www.baeldung.com/configuration-properties-in-spring-boot

[12] https://www.baeldung.com/spring-cloud-openfeign

[13] https://springdoc.org/v2/#features
