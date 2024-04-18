package com.awbd.lab6.mappers;

import com.awbd.lab6.domain.Participant;
import com.awbd.lab6.dtos.ParticipantDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-10T06:16:57+0300",
    comments = "version: 1.6.0.Beta1, compiler: javac, environment: Java 20.0.1 (Oracle Corporation)"
)
@Component
public class ParticipantMapperImpl implements ParticipantMapper {

    @Override
    public ParticipantDTO toDto(Participant participant) {
        if ( participant == null ) {
            return null;
        }

        ParticipantDTO participantDTO = new ParticipantDTO();

        return participantDTO;
    }

    @Override
    public Participant toParticipant(ParticipantDTO participantDTO) {
        if ( participantDTO == null ) {
            return null;
        }

        Participant participant = new Participant();

        return participant;
    }
}
