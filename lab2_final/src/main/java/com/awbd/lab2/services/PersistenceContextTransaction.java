package com.awbd.lab2.services;

import com.awbd.lab2.domain.Participant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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