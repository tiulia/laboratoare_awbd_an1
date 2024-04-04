package com.awbd.lab5.repositories;


import com.awbd.lab5.domain.Participant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.Arrays;
import java.util.List;


import static org.junit.Assert.assertFalse;


@DataJpaTest
@ActiveProfiles("h2")
@Slf4j
public class ParticipantRepositoryTest {

    ParticipantRepository participantRepository;
    @Autowired
    ParticipantRepositoryTest(ParticipantRepository participantRepository){
        this.participantRepository = participantRepository;
    }

    @Test
    public void findByName() {
        List<Participant> participants = participantRepository.findByLastNameLike("%no%");
        assertFalse(participants.isEmpty());
        log.info("findByLastNameLike ...");
        participants.forEach(participant -> log.info(participant.getLastName()));
    }

    @Test public void findByIds() {
        List<Participant> participants = participantRepository.findByIdIn(Arrays.asList(1L,2L));
        assertFalse(participants.isEmpty());
        log.info("findByIds ...");
        participants.forEach(participant -> log.info(participant.getLastName()));




    }

}
