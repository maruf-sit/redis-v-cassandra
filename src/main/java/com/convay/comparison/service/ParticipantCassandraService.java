package com.convay.comparison.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.convay.comparison.model.ParticipantCassandra;
import com.convay.comparison.model.ParticipantDTO;
import com.convay.comparison.repository.ParticipantCassandraRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ParticipantCassandraService {

    ParticipantCassandraRepository cassandraRepository;

    public void addParticipant(ParticipantDTO dto) {
        ParticipantCassandra participant = new ParticipantCassandra(dto.roomId(), UUID.randomUUID(), dto.name(), dto.backend());

        cassandraRepository.save(participant);
    }

}
