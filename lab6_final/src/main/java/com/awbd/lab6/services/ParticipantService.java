package com.awbd.lab6.services;

import com.awbd.lab6.dtos.ParticipantDTO;

import java.util.List;

public interface ParticipantService {

    List<ParticipantDTO> findAll();
    ParticipantDTO findById(Long l);
    ParticipantDTO save(ParticipantDTO participant);
    void deleteById(Long id);

}
