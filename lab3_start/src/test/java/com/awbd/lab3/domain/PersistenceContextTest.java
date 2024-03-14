package com.awbd.lab3.domain;


import com.awbd.lab3.services.PersistenceContextExtended;
import com.awbd.lab3.services.PersistenceContextTransaction;
import jakarta.persistence.TransactionRequiredException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=com.awbd.lab3.Lab3Application.class )
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
