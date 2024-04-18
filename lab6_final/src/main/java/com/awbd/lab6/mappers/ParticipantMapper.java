package com.awbd.lab6.mappers;

import com.awbd.lab6.domain.Participant;
import com.awbd.lab6.dtos.ParticipantDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface ParticipantMapper {
    ParticipantDTO toDto (Participant participant);
    Participant toParticipant (ParticipantDTO participantDTO);
}
