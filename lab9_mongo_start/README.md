# Project 9

- Spring Data for MongoDb
- Spring Reactive Stack.


### Reactive Manifesto

-   Architectural design pattern [1].  (Reactive systems)
-   Reactive programming: event-based, asynchronous, non-blocking programming technique. 

![External Image](https://bafybeic3soo47hitwlxqdhnjkoka3sqvjs44dhoouhc3725hukhhrs7qwu.ipfs.w3s.link/reactive1.png)

#### Responsive:
- Rapid response time.
- Usability and utility.
- Problems may be detected quickly, self-healing.
- Encourage interaction, simplifies error handing.

#### Resilient
- High availability – Replication, load balance.
- Isolation – avoid single point failure problems.
- Failures are contained within each component.
- Recovery delegated to another component.

#### Elastic
- System stays responsive under varying workload. 
- Increase/decrease resources allocated to service inputs.
- Example Google Kubernetes.
- Automatic/dynamic scaling.

#### Message driven
- Events are captured asynchronously.
- Loose coupling and isolation.
- Location transparency (scale vertically/horizontally)
- Non-blocking communication allows recipients to only consume resources while active, less system overhead.
- Functions defined when event occurs or when errors occur etc.

#### Reactive Programming/ Reactive Streams

- streams of data
- highly concurrent message consumers
- Spring implementation: Project Reactor [7]

![External Image](https://bafybeibndwbw4dpbbakrt2iqlimtcxqmhak4fk3q7f5pbsiyjhplrwit5a.ipfs.w3s.link/reactive2.png)
[11]

#### Asynchronous stream processing 
Events are produced and consumed asynchronously.  
For example, a Flux takes elements from a database and maps 
elements applying a specific operation 
or filter them according to some random criteria etc., 
only when a subscriber subscribes to the Flux.

**Interfaces**

**Publisher:** provide an unbounded 
number of elements, 
i.e. the data stream.
	
**Publisher implementations** [2]: 
- **Mono** 	publisher with 0 or 1 element in the data stream.
- **Flux** 	publisher with 0 or many elements in the data stream.	

**Subscriber**: consumes data 
from a publisher.
Consumer should be able to signal 
the producer about how much data to send (**backpressure**).

**Subscription**: links publisher 
and subscriber.

**Demand**: feedback from 
subscriber to publisher (backpressure).

![External Image](https://bafybeidxt5qt3qdwwfoeojq3omsx5nbwvne65osz54xnpbqjs4pouncsxa.ipfs.w3s.link/reactive3.png)


#### Project setup

Docker Compose is a tool for defining and running multi-container Docker applications useful for:
CI (continuous integration), CD (continuous deployment) and automated testing.

In PowerSwell download Docker image. After downloading you should find Mongo image with docker images command.
Create a docker network boot-mongo.

Run docker compose (in mongo-init) to initialize a MongoDb container.

```
docker compose up
```


#### Exercise 1 - Maven configuration
Add Maven dependencies needed to test Spring Reactive Types:

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <scope>test</scope>
</dependency>
```

#### Exercise 2
Create a test class to test Flux interface. 
Use method _just_ to create a steam of elements.
Use method _subscribe_ to indicate the method to be called on each element in the stream. 


```java
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
//...

public class ReactiveTypes1Test {

    @Test
    public void Subscriber(){
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4).log()
                .subscribe(elements::add);

        assertThat(elements).containsExactly(1, 2, 3, 4);
    }
}
```

#### Exercise 3
Implement a Subscriber that will request from publisher groups of only two elements. (_backpressure_):

```java
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.CoreSubscriber;
// ...

@Test
public void BackPressure(){
    List<Integer> elements = new ArrayList<>();

    Flux.just(1, 2, 3, 4).log()
            .subscribe(new CoreSubscriber<Integer>() {
                private Subscription s;
                int onNextAmount;

                @Override
                public void onSubscribe(Subscription s) {
                    this.s = s;
                    s.request(2);
                }

                @Override
                public void onNext(Integer integer) {
                    elements.add(integer);
                    onNextAmount++;
                    if (onNextAmount % 2 == 0) {
                        s.request(2);
                    }
                }

                @Override
                public void onError(Throwable t) {}

                @Override
                public void onComplete() {}
            });
    assertThat(elements).containsExactly(1, 2, 3, 4);
}

```

#### Exercise 4
Add a test that will map each element retrieved from a flux of integers to its double. 

```java
@Test
public void FluxMap(){
    List<Integer> elements = new ArrayList<>();

    Flux.just(1, 2, 3, 4).log()
            .map(i -> i * 2)
            .subscribe(elements::add);

    assertThat(elements).containsExactly(2, 4, 6, 8);
}
```

#### Exercise 5
Add a test that will filter movies by title.

```java
import reactor.core.publisher.Mono;

@Slf4j
public class ReactiveTypes2Test {
    @Test
    public void MonoFilter() throws Exception{
        Movie movie = new Movie();
        movie.setTitle("test movie");

        Mono<Movie> movieMono = Mono.just(movie);

        Movie movie2 = movieMono.log().
                filter(m ->m.getTitle().equalsIgnoreCase("test")).block();

        Assertions.assertThatNullPointerException()
                .isThrownBy(() -> log.info(movie2.toString()));

        Movie movie1 = movieMono.log().
                filter(m ->m.getTitle().equalsIgnoreCase("test movie")).block();
        log.info(movie1.toString());

    }
}
```

#### Exercise 6
Test delay options. 
CountDownLatch [3] has a counter field, which can be decremented. 
We can use it to block a calling thread until it's been counted down to zero.

```java
@Test
public void FluxDelay() throws Exception{
    Flux<String> fluxString = Flux.just("one","two","three");

    CountDownLatch countDownLatch = new CountDownLatch(1);

    fluxString.delayElements(Duration.ofSeconds(5)).log()
            .doOnComplete(countDownLatch::countDown)
            .subscribe(log::info);

    countDownLatch.await();
}
```

### Refactor repositories
Replace dependency: 
spring-boot-starter-data-mongodb with
dependency: **spring-boot-starter-data-mongodb-reactive**.

```
<!--
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
-->

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
```


#### Exercise 7
Change interfaces MovieRepository
to implement ReactiveMongoRepository.

```java
public interface MovieRepository extends ReactiveMongoRepository<Movie, String> {
    Flux<Movie> findByTitle(String title);


    Flux<Movie> findByYearBetween(int start, int end);

    @Query("{ 'year' : { $gt: ?0, $lt: ?1 } }")
    Flux<Movie> findByYearBetweenQ(int start, int end);


    @Query("{ 'title' : { $regex: ?0 } }")
    Flux<Movie> findByTitleRegexp(String regexp);

    Mono<Movie> findById(String title);

    Flux<Movie> findByTitleIsNotNull(Pageable pageable);
}
```


### Refactor Services

#### Exercise 8
Change return types of methods in MovieService to Flux or Mono. 

```java
public interface MovieService {
    
    Flux<Movie> findAll();
    
    Mono<Movie> findById(String id);
    
    Mono<Void> deleteById(String id);
    
    Mono<Page<Movie>> findPaginated(Pageable pageable);
}
```

Pages will be returned as Mono < Page >. The stream will contain only one element of type Page < Movie >.
The page will collect the elements present in the Flux delivered by the ReactiveMongoRepository.
The total number of elements is zipped in the page after calling count of all movies in the repository (reactiveMovieRepository.count()), which returns a Mono < Long > .

```java
@Service
public class MovieServiceImpl implements MovieService {

    MovieRepository reactiveMovieRepository;

    public MovieServiceImpl(MovieRepository reactiveMovieRepository) {
        this.reactiveMovieRepository = reactiveMovieRepository;
    }

    @Override
    public Mono<Movie> findById(String id) {

        return reactiveMovieRepository.findById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        reactiveMovieRepository.deleteById(id).subscribe();
        return Mono.empty();
    }

    @Override
    public Flux<Movie> findAll() {
        return reactiveMovieRepository.findAll();
    }

    @Override
    public Mono<Page<Movie>> findPaginated(Pageable pageable) {
        return this.reactiveMovieRepository.findByTitleIsNotNull(pageable)
                .collectList()
                .zipWith(this.reactiveMovieRepository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

}
```

### Refactor Controller

Change spring-boot-starter-web dependency.

```
<dependency>
    <groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

#### Exercise 9
Change the RequestMapping in IndexController.
Use **IReactiveDataDriverContextVariable** [10]
The presence of a variable of this type in the context sets the engine into data-driven mode, and only one of these variables is allowed to appear in the context for template execution.

Using Reactive Streams terminology, this makes Thymeleaf act as a Processor, given it will be a Subscriber to the data-driver stream, and at the same time a Publisher of output buffers (usually containing HTML markup).

Templates executed in data-driven mode are expected
to contain some kind iteration on the data-driver variable, normally by means of a th:each attribute. This iteration should be unique. Also note that, if this iteration is not present (or it doesn't end up being executed due to template logic), it is not guaranteed that the data-driven stream will not be consumed anyway -at least partially-depending on the use that the specific server implementation might make of the Reactor's back-pressure mechanism.

```java
@RequestMapping({"", "/", "/index"})
public String getIndexPage(Model model){

    IReactiveDataDriverContextVariable reactiveDataDrivenMode =
            new ReactiveDataDriverContextVariable(movieService.findAll(), 10);

    model.addAttribute("movies", reactiveDataDrivenMode);
    return "movieList";
}
```

#### Exercise 10
Change MovieController. Interface Rendering [12] is 
supported as a return value in Spring WebFlux 
controller. Its usage is comparable to the 
use of ModelAndView as a return value 
in Spring MVC controllers. Rendering can be used to combine a view name with model attributes, set the HTTP status or headers, and for other more advanced options around redirect scenarios.

```java
@Controller
@RequestMapping("/movie")
public class MovieController {

    MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    
    @RequestMapping("/info/{id}")
    public Mono<Rendering> showById(@PathVariable String id){

        Mono<Movie> movie = movieService.findById(id);
        return Mono.just(Rendering.view("movieinfo")
                .modelAttribute("movie", movie).build());
    }


    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable String id){
        movieService.deleteById(id).block();
        return "redirect:/index";
    }

    @RequestMapping({""})
    public Mono<Rendering> getMoviePage(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Mono<Page<Movie>> moviePage = movieService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        return Mono.just(Rendering.view("moviePaginated")
                .modelAttribute("moviePage", moviePage).build());

    }
}
```

### Refactor ControllerAdvice

#### Exercise 11
Modify GlobalExceptionHandler.

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<Rendering> handlerNotFoundException(Exception exception){

        return Mono.just(Rendering.view("notFoundException")
                .modelAttribute("exception", exception).build());

    }

}
```

#### Exercise 12
Modify the RequestMapping showById.

```java
@RequestMapping("/info/{id}")
public Mono<Rendering> showById(@PathVariable String id){

    return movieService.findById(id)
            .flatMap(movie -> Mono.just(Rendering.view("movieInfo")
                    .modelAttribute("movie", movie)
                    .build()))
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Movie not found for id: " + id)));
}
```


### Docs

[1] https://www.reactivemanifesto.org/

[2] https://www.baeldung.com/reactor-core
 
[3] https://www.baeldung.com/java-countdown-latch

[4] https://www.baeldung.com/spring-data-mongodb-reactive

[5] https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html

[6] https://www.baeldung.com/spring-boot-reactor-netty

[7] https://projectreactor.io/docs/core/release/reference/
 
[8] https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html

[9] https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html

[10] https://www.thymeleaf.org/apidocs/thymeleaf-spring5/3.0.5.M3/org/thymeleaf/spring5/context/webflux/IReactiveDataDriverContextVariable.html

[11] https://spring.io/reactive

[12] https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/result/view/Rendering.html


