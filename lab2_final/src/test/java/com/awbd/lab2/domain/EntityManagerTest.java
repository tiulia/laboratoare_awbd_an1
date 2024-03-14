package com.awbd.lab2.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("h2")
public class EntityManagerTest {

    //@Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void findProduct() {
        System.out.println(entityManager.getEntityManagerFactory());
        Product productFound = entityManager.find(Product.class, 1L);
        assertEquals(productFound.getCode(), "PCEZ");
    }


}
