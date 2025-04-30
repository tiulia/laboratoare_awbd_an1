package com.awbd.lab5.mappers;

import com.awbd.lab5.domain.Participant;
import com.awbd.lab5.dtos.ParticipantDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface ParticipantMapper {
    ParticipantDTO toDto (Participant participant);
    Participant toParticipant (ParticipantDTO participantDTO);
}
