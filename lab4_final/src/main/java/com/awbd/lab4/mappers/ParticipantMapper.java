package com.awbd.lab4.mappers;

import com.awbd.lab4.domain.Participant;
import com.awbd.lab4.dtos.ParticipantDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")

public interface ParticipantMapper {
    ParticipantDTO toDto (Participant participant);
    Participant toParticipant (ParticipantDTO participantDTO);
}
