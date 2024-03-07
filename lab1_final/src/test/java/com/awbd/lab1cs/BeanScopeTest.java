package com.awbd.lab1cs;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanScopeTest {

    @Test
    public void testFeatures(){
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SubscriptionConfig.class);

        MoviesSubscription myMoviesSubscription = context.getBean("myMoviesSubscription", MoviesSubscription.class);
        myMoviesSubscription.addFeature("automoated recurring billing");

        SportSubscription mySportSubscription = context.getBean("mySportSubscription", SportSubscription.class);
        mySportSubscription.addFeature("automoated invoicing");

        System.out.println(myMoviesSubscription.features);
        System.out.println(mySportSubscription.features);

        context.close();
    }

    @Test
    public void testInvoice(){
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SubscriptionConfig.class);

        BooksSubscription myBooksSubscription1 = context.getBean(BooksSubscription.class);
        Invoice invoice1 = myBooksSubscription1.getInvoice();

        BooksSubscription myBooksSubscription2 = context.getBean(BooksSubscription.class);
        Invoice invoice2 = myBooksSubscription2.getInvoice();

        System.out.println(invoice1);
        System.out.println(invoice2);

        context.close();
    }

}
