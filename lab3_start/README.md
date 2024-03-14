
# Project 3

This project contains examples of using JPA (Java Persistence API):

- working with enumerations. 
- cascading types.
- creating repositories.
- working with Docker images and containers.


### Project configuration
This project was generated with Spring Initializr. Details about the sources in domain are found in project2. 
https://start.spring.io/.


![External Image](https://bafybeigkeme7uhrm2fenjx5kyrfcqz3525g3dpxrnn2piuq2bjku2zk4fu.ipfs.w3s.link/erd_jpa.png)

### Enumerations

To allow more flexibility and more readability,
string enums can replace number enums. We can also enrich the enum class
with a descriptor attribute. 
String-based enums are more resilient to changes in the set of enum values over time. 
If new enum values are added or existing ones are modified, string-based enums do not require changes to the underlying database schema. 
In contrast, integer-based enums may require schema modifications if enum values are added or renumbered, leading to potential data migration issues.

#### Exercise 1
Change the definition of enum _Currency_.


```java
public enum Currency {
    USD("USD $"), EUR("EUR"), GBP("GBP");

    private String description;

    public String getDescription() {
        return description;
    }

    Currency(String description) {
        this.description = description;
    }
}
```

#### Exercise 2
Add @Enumerated annotation for attributes of type Currency. 
Alter the type of column .

```java
@Enumerated(value = EnumType.STRING)
private Currency currency;
```


```sql
alter table product modify column currency VARCHAR(5);
alter table product modify column currency ENUM('USD', 'EUR', 'GBP');
```

### Repositries
A repository is a high-level interface that provides a set of methods for performing common CRUD operations.
Repositories simplify transaction management. EntityManager requires manual transaction control.
EntityManager provides fine-grained control over persistent entities' lifecycle by managing detached and transient entities,
but Repositories provide caching options and predefined query methods, optimizing both the performance and the development process of the applications.

Spring repositories are interfaces that extend
the generic interface org.springframework.data.repository.Repository.

![External Image](https://bafybeiexvy3z5nwpx2m6tiowtpt5xlhh5t2zquicfib5ibkjwnsb254tjq.ipfs.w3s.link/repositories.png)



#### Exercise 3
Add a new package: src.main.java.awbd.lab3.repositories.
Add CrudRepository/PagingAndSortingRepository implementation for all entities: 
ParticipantRepository, ProductRepository, CategoryRepository.

```java 	
package com.awbd.lab3.repositories;
import com.awbd.lab3.domain.Category;
import org.springframework.data.repository.CrudRepository;
public interface CategoryRepository extends CrudRepository<Category, Long> {

}
```


```java 
package com.awbd.lab3.repositories;

import com.awbd.lab3.domain.Participant;
import org.springframework.data.repository.CrudRepository;
public interface ParticipantRepository extends CrudRepository<Participant, Long> {

}
```

```java 
package com.awbd.lab3.repositories;
import com.awbd.lab3.domain.Product;

import org.springframework.data.repository.PagingAndSortingRepository;
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

    Optional<Product> findById (Long id);
    Optional<Product> findByName (String name);

    Product save(Product product);
}
```


### Entities Cascade Types
Cascade types specify how state changes are propagated from Parent entity to Child entities.

**JPA cascade types**:
- **ALL:** propagates all operations from a parent to a child entity.
  PERSIST propagates persist operation.

- **PERSIST:** option is used only for new entities (TRANSIENT entities) for which there is no associated record in the database.
  SQL insert statements are propagated to child entities.

- **MERGE:** propagates merge operation.
  Merge operation is used only for DETACHED entities, to reattach entities in the context and perform update to the associated database records.
  SQL update statements are propagated to child entities.

- **REMOVE:** propagates remove/delete operations.
  SQL delete statements are propagated to child entities.

- **REFRESH:** if parent entity is re-read from the database, child entity is also re-read form the database.

- **DETACH:** if parent entity is removed from the context, child entity is also removed from the context.


#### Exercise 4
Add a test to check cascade type PERSIST. The test will pass only if we set CascadeType for the relationship product-info.

```java 	
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("mysql")
public class CascadeTypesTest {

  @Autowired
  ProductRepository productRepository;
  
  @Test
  public void insertProduct(){
    Product product = new Product();
    product.setName("The Vase of Tulips");
    product.setCurrency(Currency.USD);

    Info info = new Info();
    info.setDescription("Painting by Paul Cezanne");

    product.setInfo(info);
    productRepository.save(product);

    Optional<Product> productOpt = productRepository.findByName("The Vase of Tulips");
    product = productOpt.get();
    assertEquals(Currency.USD, product.getCurrency());
    assertEquals("Painting by Paul Cezanne", product.getInfo().getDescription());

  }

}
```

```java
@OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
private Info info;
```

#### Exercise 5
Add a test class for ProductRepository. The test will use save and find methods.

```java 
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("mysql")
public class CascadeTypesTest {
    //...

    @Test
    public void updateDescription() {
        Optional<Product> productOpt = productRepository.findById(1L);
        Product product = productOpt.get();
        product.getInfo().setDescription("Painting by Paul Cezanne");
        product.setCurrency(Currency.USD);

        productRepository.save(product);

        productOpt = productRepository.findById(1L);
        product = productOpt.get();
        assertEquals(Currency.USD, product.getCurrency());
        assertEquals("Painting by Paul Cezanne", product.getInfo().getDescription());

    }
}
```

#### Exercise 6
Add a test that will update the currency for all products 
linked to a certain seller.

```java 
@Test
public void updateParticipant(){
Optional<Product> productOpt = productRepository.findById(2L);

    Participant participant = productOpt.get().getSeller();
    participant.setFirstName("William");
    participant.changeCurrency(Currency.GBP);

    Product product = new Product();
    product.setName("The Vase of Tulips");
    product.setCurrency(Currency.GBP);
    participant.getProducts().add(product);

    participantRepository.save(participant);

    Optional<Participant> participantOpt = participantRepository.findById(2L);
    participant = participantOpt.get();
    participant.getProducts().forEach(prod -> {
        assertEquals(Currency.GBP, prod.getCurrency());});

}
```

The test will pass only if we set the cascade type for the relationship
participant-product.

```java 
@OneToMany(mappedBy = "seller", cascade = CascadeType.MERGE)
private List<Product> products;
```

#### Exercise 7
Add orphanRemoval attribute to the annotation OneToMany for the relationship participant-product.
If OrphanRemoval attribute is set to true, a remove entity state transition is triggered for the child entity, when it is no longer referenced by its parent entity.

```java 
@OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Product> products;
```

A test that will delete a participant and check if that product linked to the removed participant is also removed.

```java 
@Test
public void deleteParticipant(){
    participantRepository.deleteById(2L);
    Optional<Product> product = productRepository.findById(2L);

    //without orphan removal
    //assertFalse(product.isEmpty());

    //wiht orphan removal true
    assertTrue(product.isEmpty());
}
```




### Finder methods 

Interfaces extending CrudRepository may include finder methods with the following naming convention:

findByAttribute**Keyword**Attribute

**Keyword** is one of the following:
And, Or, Like, IsNot, OrderBy, GreaterThan, IsNull, StartingWith etc.

Examples:
- findByName(String name) - - WHERE name = name.
- findByNameAndDescription(String name, String desc) - - WHERE name = name or description = desc
- findByNameLike(String name) - - WHERE name LIKE ‘name%'.
- findByValueGraterThan(Double val) - - WHERE values > val
- findByNameOrderByNameDesc(String name) - - ORDER BY name DESC

SpringData JPA will automatically generate implementations for these methods.

#### Exercise 8
Add finder methods in ParticipantRepository class:
```java
List<Participant> findByLastNameLike(String lastName);
List<Participant> findByIdIn(List<Long> ids);
```

#### Exercise 9
Add a test class ParticipantRepositoryTest.

```java
@DataJpaTest
@ActiveProfiles("h2")
@Slf4j
public class ParticipantRepositoryTest {

    ParticipantRepository participantRepository;

    @Autowired
    ParticipantRepositoryTest(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }


    @Test
    public void findByName() {
        List<Participant> participants = participantRepository.findByLastNameLike("%no%");
        assertFalse(participants.isEmpty());
        log.info("findByLastNameLike ...");
        participants.forEach(participant -> log.info(participant.getLastName()));
    }
}
```

#### Exercise 10
Add a test for findByIdIn method.
```java
@Test public void findByIds() {
    List<Participant> participants = participantRepository.findByIdIn(Arrays.asList(1L,2L));
    assertFalse(participants.isEmpty());
    log.info("findByIds ...");
    participants.forEach(participant -> log.info(participant.getLastName()));
}
```

### Query annotation

Custom SQL query may be defined using @Query annotation.
Queries are written in JPQL or in native sql. For native sql attribute native: _Query ( , native = true)_ must be added to the Query annotation.

JPQL is an object-oriented query language. It uses the entity objects to define operations on the database records. 
JPQL queries are transformed to SQL.
There are two ways of transferring parameters to queries:

**Indexed Query Parameters**
Spring Data will pass method parameters to the query in the same order they appear in the method declaration:

```java
@Query("select p from Product p where p.seller.firstName = ?1 and p.seller.lastName = ?2") 
List<Product> findBySellerName(String sellerFirstName, String sellerLastName);
```

**Named Parameters** We use the @Param annotation in the method declaration to match parameters defined by name in JPQL with parameters from the method declaration:
```java
@Query("select p from Product p where p.seller.firstName = :firstName and p.seller.lastName = :lastName") 
List<Product> findBySellerName(@Param("firstName") String sellerFirstName, @Param("lastName") String sellerLastName);
```

#### Exercise 11
Add a method findBySeller in ProductRepository class.
```java
@Query("select p from Product p where p.seller.id = ?1")
List<Product> findBySeller(Long sellerId);
```

#### Exercise 12
Add method findBySellerName wiht Named Parameters in ProductRepository 
and create a test method in class ProductRepositoryTest.

```java
@Query("select p from Product p where p.seller.firstName = :firstName and p.seller.lastName = :lastName")
List<Product> findBySellerName(@Param("firstName") String sellerFirstName, @Param("lastName") String sellerLastName);
```

```java
@DataJpaTest
@ActiveProfiles("h2")
@Slf4j
public class ProductRepositoryTest {
ProductRepository productRepository;
@Autowired
ProductRepositoryTest(ProductRepository productRepository){
this.productRepository = productRepository;
}

    @Test
    public void findProducts() {
        List<Product> products = productRepository.findBySeller(1L);
        assertTrue(products.size() >= 1);
        log.info("findBySeller ...");
        products.forEach(product -> log.info(product.getName()));
    }

    @Test
    public void findProductsBySellerName() {
        List<Product> products = productRepository.findBySellerName("Will","Snow");
        assertTrue(products.size() >= 1);
        log.info("findBySeller ...");
        products.forEach(product -> log.info(product.getName()));
    }

}
```

### Docker

Docker is a toolkit for container management.

•	Platform for developing, shipping, and running applications.
•	Separates applications from infrastructure.
•	Run on physical or virtual machines, in a data center, on cloud providers etc.

•	Runs application in isolated environment, in containers. 
•	Develop, test, deploy using containers.
•	CI/CD continuous integration, continuous delivery.

**Docker components**:

•	Server or daemon process, dockerd command.
•	REST API interfaces to daemon.
•	Command line interface, CLI client  docker command.

**Docker objects**: 

•	**Images**: read-only template with instructions to create a container. Images are published in a docker registry. To build an image a Dockerfile is created, with instructions for each layer of the image. Rebuilding an image affects only those layers changed in the Dockerfile.

•	**Container**: runnable instances of an image. By default, containers can connect to external networks using the host machine’s network connection. 

•	**Networks**, **Volumes** etc.

#### Exercise 13
Add and run a maven configuration.
```
clean install -Dmaven.test.skip=true
```

#### Exercise 14
Create a docker file to build a docker image 
for the runnable jar build in the previous step.
We use openjdk image as base image.
We copy the app.jar and as entry point we run the jar.
The entry point is the command that will be executed when a container based on this image starts.
```
FROM openjdk:latest
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```


#### Exercise 15
Build the image based on the docker file.
Run a docker configuration in the IDE or
Run in the directory containing the docker file

```
docker build -t lab3 .
```

To check available images run:
```
docker images
```

#### Exerise 16
Run a container based on the docker image lab3

```
docker run -e "SPRING_PROFILES_ACTIVE=h2" --name lab3_h2 -p 8080:8080 lab3
```

To stop the container run
```
docker stop lab3_h2
```

To remove the container run
```
docker rm lab3_h2
```

#### Exerise 17
Run a docker image for mysql. Run the container in a network boot-mysql.

```
docker pull mysql
docker network create boot-mysql
docker run --name mysql_awbd --network boot-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=awbd -e MYSQL_PASSWORD=awbd -e MYSQL_USER=awbd mysql
```

#### Exerise 18
Create a new file application-sqldocker.properties. 
This file will be used for the configuration of the data-source 
when the application will run with profile sqldocker.

```
spring.datasource.url=jdbc:mysql://mysql_awbd :3306/awbd
spring.datasource.username=awbd
spring.datasource.password=awbd


spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode = never
spring.sql.init.platform=mysql
```

#### Exerise 19
Run the maven configuration clean-install.
Run a container based on the docker image lab3 with the profile sqldocker.

```
docker run -e "SPRING_PROFILES_ACTIVE=sqldocker" --name lab3_sqldocker -p 8080:8080 lab3
```

#### Exerise 20
Connect to docker container boot-mysql. First find the container, then use bash to connect to mysql. 

```
docker ps -a
docker exec -it [container_id] bash
mysql -u root -p
use awbd
select * from participant
```
