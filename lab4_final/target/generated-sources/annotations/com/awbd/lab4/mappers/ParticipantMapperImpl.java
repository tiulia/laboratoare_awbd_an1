package com.awbd.lab4.mappers;

import com.awbd.lab4.domain.Participant;
import com.awbd.lab4.domain.Product;
import com.awbd.lab4.dtos.ParticipantDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-03T15:10:24+0300",
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

        participantDTO.setId( participant.getId() );
        participantDTO.setLastName( participant.getLastName() );
        participantDTO.setFirstName( participant.getFirstName() );
        participantDTO.setBirthDate( participant.getBirthDate() );
        List<Product> list = participant.getProducts();
        if ( list != null ) {
            participantDTO.setProducts( new ArrayList<Product>( list ) );
        }

        return participantDTO;
    }

    @Override
    public Participant toParticipant(ParticipantDTO participantDTO) {
        if ( participantDTO == null ) {
            return null;
        }

        Participant participant = new Participant();

        participant.setId( participantDTO.getId() );
        participant.setLastName( participantDTO.getLastName() );
        participant.setFirstName( participantDTO.getFirstName() );
        participant.setBirthDate( participantDTO.getBirthDate() );
        List<Product> list = participantDTO.getProducts();
        if ( list != null ) {
            participant.setProducts( new ArrayList<Product>( list ) );
        }

        return participant;
    }
}
