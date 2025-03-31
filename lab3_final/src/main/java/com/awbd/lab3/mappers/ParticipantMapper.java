package com.awbd.lab3.mappers;

import com.awbd.lab3.domain.Participant;
import com.awbd.lab3.dtos.ParticipantDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface ParticipantMapper {
    ParticipantDTO toDto (Participant participant);
    Participant toParticipant (ParticipantDTO participantDTO);
}
