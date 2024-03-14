
# Project 2

This project contains examples of using JPA (Java Persistence API):

- working with an embedded in-memory database, H2, or with a relational MySQL database. 
- creating entities and relationships.
- managing entities' lifecycle.
- running tests, working with @DataJpaTest.


### Project configuration

This project was generated with Spring Initializr. The following dependencies were added: Spring Data JPA, H2 Database, Lombok, MySQL Driver, Spring Web.
https://start.spring.io/. We will add JPA annotation to the POJOs in the ERD schema below.


![External Image](https://bafybeigkeme7uhrm2fenjx5kyrfcqz3525g3dpxrnn2piuq2bjku2zk4fu.ipfs.w3s.link/erd_jpa.png)

### Lombok annotations

Lombok generates code that is commonly used in Java plain objects, like setters, getters, constructors, toString, equals or hashCode functions or logging options.
It reduces boilerplate code and may be easily integrated in IDEs. With Lombok's plugins that support Lombok features, the generated code is automatically and immediately available. In IntelliJ you may find the Lombok plugin under Refactor menu.   

#### Exercise 1
Annotate all classes with Lombok.Data. @Data has the same effect as adding: @EqualsAndHashCode, @Getter, @Setter, @ToString. It also adds a constructor taking as arguments all @NonNull and final fields.
Try "Refactor – Delombok" to see the equivalent Java Code.


```java
@Data
public class Category {

    private Long id;
    private String name;

    private List<Product> products;

}
```

### H2 database configuration

H2 in-memory RDBMS (relational database management system), can be embedded in Java Applications. It supports standard SQL and JDBC API.  
In-memory databases rely on main memory for data storage, in contrast to databases that store data on disk or SSDs, hence in-memory databases are faster than traditional RDBMS obtaining minimal response time by eliminating the need to access disks.
If maven dependency for H2 is added in pom.xml Spring autoconfigures an H2 instance.

#### Exercise 2

Enable H2 database console and configure the data source in the application.properties file.
If H2 console is enabled, by setting the property spring.h2.console.enabled, we may access the url:
http://localhost:8080/h2-console

If property spring.datasource.url=jdbc:h2:mem:testdb is set, a database named testdb will be embedded in the application, notice also the properties to set up dirver and credentials.



```xml
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

```

### Entities

**@Entity** JPA Entities are POJOs that can be persisted in the database. Each entity represents a table. Every instance of a class annotated with @Entity represents a row in the table.

**@Id**
All entities must have a primary key. The filed annotated with @Id represents the primary key.
For each primary key it is mandatory to define a generation strategy. There are four possible generation strategies:
- GenerationType.**AUTO**		Spring chooses strategy.
- GerationType.**IDENTITY** 	auto-incremented value.	
- GerationType.**SEQUENCE**	uses a sequence if sequences are supported by the database (for example in Oracle databases). 
- GerationType.**TABLE**		uses a table to store generated values.

For the last two generation strategies we must specify a generator (sequence or table):
```java 	
@GeneratedValue(strategy = GenerationType.TABLE, generator = "table-generator")
@TableGenerator(name = "table-generator", …)
```

#### Exercise 3

Annotate all classes with @Entity. Also annotate key attributes with @Id and @GeneratedValue. Re-run the application and check that tables CATEGORY, PRODUCT, PARTICIPANT and INFO are created in the H2 database.

```java 	
@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private Double reservePrice;
    private Boolean restored;
}

```

### Relationships

**@OneToOne** links two tables based on a FK column. In the child table a foreign key value references the primary key from a row in the parent table. 
Each row in the child table is linked to exactly one row in the parent table, in other words, each instance of the child @Entity is linked to exactly one instance of the parent @Entity.

OneToOne relationships can be either unidirectional or bidirectional. 	
For instance, unidirectional relationship product – info means that the entity _Product_ will provide access to entity _Info_, but _Info_ entity we will not provide access to a product. In the associated tables in the RDBMS we will add info_id column in table product, but we will not add product_id column in table info.

**@OneToMany** specifies that one entity is associated with one or more entities. This type of relationship is modelled by List, Set, Map, SortedSet, SortedMap collections. The foreign key is added in the table corresponding to “many”. For instance, Participant-Product is a one-to-many relationship. The foreign key is added in the Product table.

**@ManyToOne** is pairing a relationship of type @OneToMany.

**@JoinColum** defines the foreign key. In Product we add:

```java 	
@ManyToOne
@JoinColumn(name="participant_id")
private Participant participant;
```

The attribute **mappedBy** defines the corresponding field in the corresponding Many-To-One relationship. 
(@ManyToOne relationship) in Participant we have:

```java
@OneToMany(mappedBy = "participant")
private List<Product> products;
```

**@ManyToMany** is defined by an association table. For instance, the relationship product-category is modelled by the table product_category with columns: product_id, category_id.

**@JoinTable** defines the association table. In @Entity Category we have:

```java
@JoinTable(name = "product_category", 
        joinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
private List<Product> products;
```

The attribute mappedBy defines the corresponding field in the assciated Many-To-Many relationship. 			(@ManyToMany relationship) in @Entity Product we have:
```java
@ManyToMany(mappedBy = "products")
private List<Category> categories;
```

#### Exercise 4
Add a @OnoToOne relationship between product and info entities.
In the entity Product add filed _info_.

```java
@OneToOne
private Info info;
```

In the entity Info add filed _product_.
```java
@OneToOne(mappedBy = "info")
private Product product;
```


Run the application and check that the columns product_id and info_id are added in the tables info and product.

#### Exercise 5
Add a @OneToMany and a @ManyToOne relationships between the entities Participant and Product.

In Participant add:

```java
@OneToMany(mappedBy = "seller")
private List<Product> products;
```

In Product add:
```java
@ManyToOne
private Participant seller;
```

Run the application. Check that the column seller_id is added in the table product.

#### Exercise 6
Add a @ManyToMany relationship Product-Category:

In Product add:
```java
@ManyToMany(mappedBy = "products")
private List<Category> categories;
```

In Category add:
```java
@ManyToMany
@JoinTable(name = "product_category",
        joinColumns =@JoinColumn(name="category_id",referencedColumnName = "id"),
        inverseJoinColumns =@JoinColumn(name="product_id",referencedColumnName="id"))
private List<Product> products;
```

Run the application and check the creation of table product_catgory.

### Database initialization

Hibernate options: For test purposes a file **data-h2.sql** may be added in resources. The sql statements added in this file will be used to initialize the database.

**spring.jpa.hibernate.ddl-auto** specifies options for database initialization:
- **none**: applications start without database initialization
- **create**: tables are dropped and recreated at startup. A table is created for each class annotated with @Entity.
- **create-drop**: tables are created at startup and dropped when application stops. This is the default value for embedded databases.
- **validate**: application starts if all tables corresponding to entities exist and match entities specifications. 
- **update**: Hibernates updates schema if tables differ from entities specifications.

Spring Boot options: The files 
- schema-[platform].sql containing DDL statements and 
- data-[platform].sql containing LMD statements 

may be used to create and initialize the database.
The suffix platform is set by **spring.sql.init.platform**.
**spring.sql.init.mode** controls initialization behaviour: always, never of embedded 


#### Exercise 7 
Test **spring.jpa.hibernate.ddl-auto**=create | create-drop and **import.sql**

Add the file data-h2.sql. We will add data in tables: category, participant, product, product_category.

```sql
insert into category(id, name) values(1, 'paintings');
insert into participant(id, last_name, first_name) values(1, 'Adam', 'George');
insert into product (id, name, code, reserve_price, restored, seller_id) values (1, 'The Card Players', 'PCEZ', 250, 0, 1);
insert into product_category values(1,1);
```

#### Exercise 8
Reorganize application's properties in the following files:
- application.properties:
```
spring.profiles.active=h2
spring.jpa.show-sql=true
```
- **application-h2.properties**

```
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```
- **application-mysql.properties**

```
spring.datasource.url=jdbc:mysql://localhost:3306/awbd
spring.datasource.username=awbd
spring.datasource.password=awbd
spring.jpa.hibernate.ddl-auto=create-drop
```

We may use two profiles: h2 and mysql. 
Add different run-configurations for profiles h2 and mysql.
Run the application with profiles h2 and mysql. 

#### Exercise 9
Test **spring.sql.init.mode** and **data-h2.sql, schema-h2.sql**.

Add the file schema-h2.sql. Rename the file import.sql -> data-h2.sql and test spring.sql.init.mode options.

```
spring.jpa.hibernate.ddl-auto=none
#h2 profile
spring.sql.init.mode = embedded
#mysql profile
spring.sql.init.mode = always
```

#### Exercise 10
We may use different initialization files. 
Rename file data-h2.sql -> data-h2.sql
Rename file schema-h2.sql -> schema-h2.sql.

Add analogous files for mysql. Add drop statements for mysql schema.

```
DROP TABLE IF EXISTS product_category;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS info;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS participant;
```

Add in corresponding .properties files spring.sql.init.platform option.

```
spring.sql.init.platform=h2
```

```
spring.sql.init.platform=mysql
```

To start the application without initializing the database use:
```
spring.jpa.hibernate.ddl-auto=validate
spring.sql.init.mode = never
```


## Factory design pattern

![External Image](https://bafybeibhleief3zvjkdvsc63om2b4k4y6bin4x4ulhkos6gvic5kdfpnle.ipfs.w3s.link/factorypattern.png)

The EntityManagerFactory is a component in Java Persistence API (JPA) responsible for creating EntityManager instances based on persistence configuration. An EntityManager is used to interact with the database via a PersistenceContext.
Persistence context keeps track of all the changes made into managed entities.
There are two types of persistence contexts:
- Transaction-scoped persistence context (default): When a transaction completes all changes are flushed into persistent storage.
- Extended-scoped persistence context: An extended persistence context can span across multiple transactions. We can persist the entity without the transaction but cannot flush it without a transaction.

#### Exercise 11
Create a test class in a new package com.awbd.lab2.domain.
```java
@DataJpaTest
@ActiveProfiles("h2")
public class EntityManagerTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void findProduct() {
        System.out.println(entityManager.getEntityManagerFactory());
        Product productFound = entityManager.find(Product.class, 1L);
        assertEquals(productFound.getCode(), "PCEZ");
    }
}
```

#### Exercise 12
Create a package com.awbd.lab2.services and two classes PersistenceContetExtended and PersistenceContextTransaction.

```java
@Service
public class PersistenceContextExtended {
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Transactional
    public Participant updateInTransaction(Long participantId, String name) {
        Participant updatedParticipant = entityManager.find(Participant.class, participantId);
        updatedParticipant.setFirstName(name);
        entityManager.persist(updatedParticipant);
        return updatedParticipant;
    }
    public Participant update(Long participantId, String name) {
        Participant updatedParticipant = entityManager.find(Participant.class, participantId);
        updatedParticipant.setFirstName(name);
        entityManager.persist(updatedParticipant);
        return updatedParticipant;
    }
    public Participant find(long id) {

        return entityManager.find(Participant.class, id);
    }
}
```

```java
@Service
public class PersistenceContextTransaction {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Participant updateInTransaction(Long participantId, String name) {
        Participant updatedParticipant = entityManager.find(Participant.class, participantId);
        updatedParticipant.setFirstName(name);
        entityManager.persist(updatedParticipant);
        return updatedParticipant;
    }

    public Participant update(Long participantId, String name) {
        Participant updatedParticipant = entityManager.find(Participant.class, participantId);
        updatedParticipant.setFirstName(name);
        entityManager.persist(updatedParticipant);
        return updatedParticipant;
    }

    public Participant find(long id) {
        return entityManager.find(Participant.class, id);
    }
}
```

#### Exercise 13

Add tests:

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes=com.awbd.lab2.Lab2Application.class )
@ActiveProfiles("h2")
public class PersistenceContextTest {

    @Autowired
    PersistenceContextExtended persistenceContextExtended;

    @Autowired
    PersistenceContextTransaction persistenceContextTransction;
    
    @Test(expected = TransactionRequiredException.class)
    public void persistenceContextTransctionThrowException() {
        persistenceContextTransction.update(1L, "William");
    }
    
    @Test
    public void persistenceContextTransctionExtended() {
        persistenceContextTransction.updateInTransaction(1L, "William");
        Participant participantExtended = persistenceContextExtended.find(1L);
        System.out.println(participantExtended.getFirstName());
        assertEquals(participantExtended.getFirstName(), "William");
    }


    @Test
    public void persistenceContextExtendedExtended() {
        persistenceContextExtended.update(1L, "Snow");
        Participant participantExtended = persistenceContextExtended.find(1L);
        System.out.println(participantExtended.getFirstName());
        assertEquals(participantExtended.getFirstName(), "Snow");
    }

    @Test
    public void persistenceContextExtendedTransaction() {
        persistenceContextExtended.update(1L, "Will");
        Participant participantTransaction = persistenceContextTransction.find(1L);
        System.out.println(participantTransaction.getFirstName());
        assertNotEquals(participantTransaction.getFirstName(), "Will");
    }

}

```

#### Exerise 14

Add actuator dependency in pom.xml and check the beans that are created in the application.

```		
<dependency>
    <groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```


### References
https://www.baeldung.com/jpa-hibernate-persistence-context
https://www.baeldung.com/spring-data-rest-relationships


