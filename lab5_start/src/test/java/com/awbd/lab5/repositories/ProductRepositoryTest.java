package com.awbd.lab5.repositories;

import com.awbd.lab5.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import static org.junit.Assert.assertTrue;

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
