package com.awbd.lab3.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("h2")
public class EntityManagerTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void findProduct() {
        System.out.println(entityManager.getEntityManagerFactory());
        Product productFound = entityManager.find(Product.class, 1L);
        assertEquals("PCEZ", productFound.getCode());
    }
}