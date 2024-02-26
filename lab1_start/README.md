
# Project 1  

This project ilustrates the three different types of depenency injection:

- constructor DI
- setter DI 
- property DI

## Examples xml configuration

#### Example 1
Review the definition in resources/applicationContext.xml of the bean with the ID 'mySportSubscription.' Review the testXmlContext JUnit tests to retrieve the beans from the context and execute the methods: getDescription() and getPrice().
```xml
<bean id="mySportSubscription"
    class="com.awbd.lab1.SportSubscription">
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
Set up the file holding properties in resources/applicationContextDI.xml.


```xml
<context:property-placeholder location = "classpath:application.properties"/>
```

#### Example 3
Review the definitions of beans with IDs 'myDiscountCalculator,' 'myBooksSubscription,' and 'myMoviesSubscription' in resources/applicationContextDI.xml. The class 'BooksSubscription' uses constructor dependency injection, while the class 'MoviesSubscription' uses setter dependency injection. We must define the bean that we will inject as 'DiscountCalculator,' i.e., 'myDiscountCalculator.'


```xml
<bean id="myDiscountCalculator" class="com.awbd.lab1.DiscountCalculatorImpl">
    <property name="percent"  value="${discount.percent}"/>
</bean>

<bean id="myBooksSubscription" class="com.awbd.lab1.BooksSubscription">
    <constructor-arg name="discountCalculator" ref="myDiscountCalculator" />
</bean>

<bean id="myMoviesSubscription"
    class="com.awbd.lab1.MoviesSubscription">
    <property name="discountCalculator" ref="myDiscountCalculator"/>
</bean>
```

#### Example 4
Run tests constructorDI and setterDI. Both use applicationContextDI.xml to configure the context.
```java
@Test
public void contructorDI(){
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContextDI.xml");

    Subscription theSubscription = context.getBean("myBooksSubscription", Subscription.class);

    System.out.println(theSubscription.getPrice() + " " + theSubscription.getDescription());

    context.close();
}
```

## Exercises Switch to Java Code configuration

#### Exercise 1

Create a new package com.awbd.lab1cs and copy all classes from com.awbd.lab1cs. Annotate all classes (BooksSubscription, MoviesSubscription, SportSubscription) with @Component.

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
Add test class com.awbd.lab1cs.ContextLoadTest. Add methods to test all types of DI.
```java
import lab1cs.Subscription;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextLoadTest {

    @Test
    public void propertyDITest(){
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContextCS.xml");
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
Add a configuration class that we will use instead of the XML file applicationContextCS. Also, in the same file, define another implementation for DiscountCalculator.
```java
class ExternalCalculator implements DiscountCalculator{
    public double calculate(int price) {
        return 0.65 * price;
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
    public void contructorDI(){
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SubscriptionConfig.class);

        Subscription myBooksSubscription = context.getBean("myBooksSubscription", Subscription.class);

        System.out.println(myBooksSubscription.getPrice() + " " + myBooksSubscription.getDescription());

        context.close();
    }
}
```
