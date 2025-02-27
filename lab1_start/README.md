
# Project 1

This project illustrates three different types of dependency injection.

- constructor DI
- setter DI
- property DI

## Examples xml configuration

#### Example 1
Review the definition in resources/applicationContext.xml of the bean with the ID 'mySportSubscription.' Review the testXmlContext JUnit tests to retrieve the beans from the context and execute the methods: getDescription() and getPrice().

```xml

<bean id="mySportSubscription"
      class="com.awbd.lab1c.SportSubscription">
</bean>

```

```java
@Test
public void testXmlContext(){

    // load the spring configuration file
    ClassPathXmlApplicationContext context =
            new ClassPathXmlApplicationContext("applicationContext.xml");

    // retrieve bean from spring container
    Subscription mySportSubscription = context.getBean("mySportSubscription", Subscription.class);

    // call methods on the bean
    System.out.println(mySportSubscription.getPrice() + " " + mySportSubscription.getDescription());

        // close the context
    context.close();
}

```
#### Example 2
Set up the file for storing properties in resources/applicationContextDI.xml.


```xml
<context:property-placeholder location = "classpath:application.properties"/>
```

#### Example 3
Review the definitions of beans with IDs 'myDiscountCalculator,' 'myBooksSubscription,' and 'myMoviesSubscription' in resources/applicationContextDI.xml. The class 'BooksSubscription' uses constructor dependency injection, while the class 'MoviesSubscription' uses setter dependency injection. We must define the bean that we will inject as 'DiscountCalculator,' i.e., 'myDiscountCalculator.'

```xml

<bean id="myDiscountCalculator" class="com.awbd.lab1c.DiscountCalculatorImpl">
    <property name="percent" value="${discount.percent}"/>
</bean>

<bean id="myBooksSubscription" class="com.awbd.lab1c.BooksSubscription">
<constructor-arg name="discountCalculator" ref="myDiscountCalculator"/>
</bean>

<bean id="myMoviesSubscription"
      class="com.awbd.lab1c.MoviesSubscription">
<property name="discountCalculator" ref="myDiscountCalculator"/>
</bean>
```

#### Example 4
Run tests constructorDI and setterDI. Both use applicationContextDI.xml to configure the context.
```java
@Test
public void constructorDI(){
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContextDI.xml");

    Subscription theSubscription = context.getBean("myBooksSubscription", Subscription.class);

    System.out.println(theSubscription.getPrice() + " " + theSubscription.getDescription());

    context.close();
}
```

![App Screenshot](https://bafybeiggoagagos7j7pdm5zuyencbsf5kuwat5dmacyvfchqgazmdvylfi.ipfs.w3s.link/project1_architecture.png)



## Exercises Switch to Java Code configuration

#### Exercise 1

Create a new package com.awbd.lab1c and copy all classes from com.awbd.lab1cs. Annotate all classes (BooksSubscription, MoviesSubscription, SportSubscription) with @Component.

```java
@Component("mySportSubscription")
public class SportSubscription implements Subscription{

}

```

#### Exercise 2
Annotate the property percent in DiscountCalculatorImpl with @Value.
```java
@Value("${discount.percent}")
double percent;
```

#### Exercise 3
Add the attribute 'discountCalculator' in SportSubscription and annotate it with @Autowired.
Add the annotation @Autowired to the setDiscountCalculator method in MoviesSubscription.

Annotate the constructor of BooksSubscription with @Autowired.
```java
@Autowired
DiscountCalculator discountCalculator;

public double getPrice() {
    return discountCalculator.calculate(1000);
}
```

```java
@Autowired
public void setDiscountCalculator(DiscountCalculator discountCalculator) {
    this.discountCalculator = discountCalculator;
}
```

```java
@Autowired
public BooksSubscription(DiscountCalculator discountCalculator) {

    this.discountCalculator = discountCalculator;
}
```

#### Exercise 4
Add @Component for the implementation DiscountCalculatorImpl.
```java
@Component
public class DiscountCalculatorImpl implements DiscountCalculator {
}
```

#### Exercise 5
Add test class com.awbd.lab1c.ContextLoadTest. Add methods to test all types of DI.

```java
package com.awbd.lab1c;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextLoadTest {

    @Test
    public void propertyDITest() {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContextC.xml");
        Subscription mySportSubscription = context.getBean("mySportSubscription", Subscription.class);
        System.out.println(mySportSubscription.getPrice() + " "
                + mySportSubscription.getDescription());
        context.close();
    }
}
```

#### Exercise 6
Create a new implementation for DiscountCalculator interface. Rerun all tests.
```java
@Component
public class FixDiscountCalculator implements DiscountCalculator{

    public double calculate(int price) {
        return 0.85 * price;
    }
}
```

#### Exercise 7
To fix the tests add @Primary annotation in FixDiscountCalculator.
```java
@Component
@Primary
public class FixDiscountCalculator implements DiscountCalculator{

    public double calculate(int price) {
        return 0.85 * price;
    }
}
```

#### Exercise 8
Add the @Qualifier annotation to the setter method in MoviesSubscription. Use it also for the constructor in BooksSubscription.
```java
@Autowired
@Qualifier("discountCalculatorImpl")
public void setDiscountCalculator(DiscountCalculator discountCalculator) {
    this.discountCalculator = discountCalculator;
}
```

```java
@Autowired
public BooksSubscription(@Qualifier("discountCalculatorImpl") DiscountCalculator discountCalculator) {
    this.discountCalculator = discountCalculator;
}

```


#### Exercise 9
Add a configuration class to replace the XML file applicationContextC. Also, in the same file, define another implementation for DiscountCalculator.
```java
class ExternalCalculator implements DiscountCalculator{
    public double calculate(int price) {
        return 0.65 * price;
    }
}

@Configuration
@ComponentScan("com.awbd.lab1c")
@PropertySource("classpath:application.properties")
public class SubscriptionConfig {

    @Bean
    public DiscountCalculator externalCalculator(){
        return new ExternalCalculator();
    }

}
```

#### Exercise 10
Change @Qualifier annotation for setter method in MoviesSubscription to inject externalCalculator and run tests:
```java
@Autowired
@Qualifier("externalCalculator")
public void setDiscountCalculator(DiscountCalculator discountCalculator) {
    this.discountCalculator = discountCalculator;
}
```

#### Exercise 11
Add a test class that will load the context using SubscriptionConfig.class. Add tests for all types of DI.
```java
public class ContextLoadConfigTest {

    @Test
    public void constructorDI(){
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SubscriptionConfig.class);

        Subscription myBooksSubscription = context.getBean("myBooksSubscription", Subscription.class);

        System.out.println(myBooksSubscription.getPrice() + " " + myBooksSubscription.getDescription());

        context.close();
    }

    @Test
    public void setterDI(){
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SubscriptionConfig.class);

        Subscription theSubscription = context.getBean("myMoviesSubscription", Subscription.class);

        System.out.println(theSubscription.getPrice() + " " + theSubscription.getDescription());

        context.close();
    }

    @Test
    public void propertyDITest() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SubscriptionConfig.class);
        Subscription mySportSubscription = context.getBean("mySportSubscription", Subscription.class);
        System.out.println(mySportSubscription.getPrice() + " "
                + mySportSubscription.getDescription());
        context.close();
    }
}
```

### Singleton pattern

Use Singleton or Dependency Injection (DI)? 

In Spring, a bean is created for each container, and an application may have multiple containers. Spring follows the 'Open for extension, closed for modification' principle, allowing the implementation of a bean to be replaced without modifying the existing code.

```java
public class LazyInitSingleton {

    private static LazyInitSingleton instance;

    private LazyInitSingleton(){}

    public static LazyInitSingleton getInstance() {
        if (instance == null) {
            instance = new LazyInitSingleton();
        }
        return instance;
    }
}
```


#### Exercise 12
Create a new interface Features with only one method addFeature

```java
public interface Features {
    void addFeature(String option);
}
```

#### Exercise 13
Create a class FeaturesImpl to store subscription features as a list of Strings.
Create a bean of type FeaturesImpl in SubscriptionConfig

```java
package com.awbd.lab1c;

class FeaturesImpl implements Features {
    private List<String> features;

    public FeaturesImpl() {
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
```

```java
public class SubscriptionConfig {
    @Bean
    public Features featureBean() {
        return new FeaturesImpl();
    }
}
```

#### Exercise 14
In MoviesSubscription and SportSubscription add Feature dependencies.

```java
Features features;

@Autowired
public void setFeatures(Features features){
    this.features = features;
}
```

```java
@Autowired
Features features;
```

#### Exercise 15
In MovieSubscription and SportSubscription add a method to add features.

```java
public void addFeature(String option){
    features.addFeature(option);
}
```
#### Exercise 16
Create and run the test testFeatures.

```java
public class BeanScopeTest {

    @Test
    public void testFeatures(){
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SubscriptionConfig.class);

        MoviesSubscription myMoviesSubscription = context.getBean("myMoviesSubscription", MoviesSubscription.class);
        myMoviesSubscription.addFeature("recurring billing");

        SportSubscription mySportSubscription = context.getBean("mySportSubscription", SportSubscription.class);
        mySportSubscription.addFeature("invoicing");

        System.out.println(myMoviesSubscription.features);
        System.out.println(mySportSubscription.features);

        context.close();
    }
}
```

#### Exercise 17
Change the default 
scope for the bean Features to 'prototype' and run again testFeatures.

```java
@Bean
@Scope("prototype")
public Features featureBean(){ return new FeaturesImpl();}
```

#### Exercise 18
Configure a bean Invoice, use 'prototype' scope.
```java
public class Invoice{
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
```

```java
@Configuration
@ComponentScan("com.awbd.lab1c")
@PropertySource("classpath:application.properties")
public class SubscriptionConfig {
    //...
    @Bean
    @Scope("prototype")
    public Invoice invoice() {return new Invoice(String.valueOf(LocalTime.now()));}

}
```

#### Exercise 19
In BooksSubscription class autowire a bean of type Invoice and add a method to get the Invoice.

```java
@Autowired
Invoice invoice;
    
public Invoice getInvoice(){ 
    return this.invoice;
}
```

#### Exercise 20
Add a test that will call getInvoice twice.

```java
@Test
public void testInvoice(){
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SubscriptionConfig.class);

    BooksSubscription myBooksSubscription1 = context.getBean(BooksSubscription.class);
    Invoice invoice1 = myBooksSubscription1.getInvoice();

    BooksSubscription myBooksSubscription2 = context.getBean(BooksSubscription.class);
    Invoice invoice2 = myBooksSubscription2.getInvoice();

    System.out.println(invoice1);
    System.out.println(invoice2);
    
    context.close();
}
```

#### Exercise 21
If we need to inject prototype beans into singleton beans we may use ApplicationContextAware.
Run the test again.

```java
@Component("myBooksSubscription")
public class BooksSubscription implements Subscription, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

 
    public Invoice getInvoice() {
        return applicationContext.getBean(Invoice.class);
    }
    //...
}
```

### Beans lifecycle

#### Exercise 22
Add the maven dependency for @PreDestroy and @PostInit annotations.

```xml
<dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.3.2</version>
</dependency>
```
#### Exercise 22
Add @PreDestroy method bean SportSubscription

```java
@PreDestroy
public void customDestroy()
{
    System.out.println("Bean SportSubscription customDestroy() invoked...");
}
```

#### Exercise 23
Modify FeaturesImpl to implement InitializingBean interface. Add a @PostConstruct method. Run test and analyze the order of custom construct and destroy methods.

```java
class FeaturesImpl implements Features, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Bean Features afterPropertiesSet() invoked... ");
    }

    @PostConstruct
    public void customInit() {
        System.out.println("Bean Features customInit() invoked...");
    }
}
```