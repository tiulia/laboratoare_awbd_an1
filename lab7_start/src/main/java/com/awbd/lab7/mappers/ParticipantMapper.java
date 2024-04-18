package com.awbd.lab7.mappers;

import com.awbd.lab7.domain.Participant;
import com.awbd.lab7.dtos.ParticipantDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface ParticipantMapper {
    ParticipantDTO toDto (Participant participant);
    Participant toParticipant (ParticipantDTO participantDTO);
}
