package com.awbd.lab3.services;

import com.awbd.lab3.dtos.ParticipantDTO;

import java.util.List;

public interface ParticipantService {

    List<ParticipantDTO> findAll();
    ParticipantDTO findById(Long l);
    ParticipantDTO save(ParticipantDTO participant);
    void deleteById(Long id);

}
