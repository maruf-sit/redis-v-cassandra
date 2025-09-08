package com.convay.comparison;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    @Transactional
    public void addParticipant(ParticipantDTO dto) {
        Participant participant = new Participant(dto.roomId(), UUID.randomUUID(), dto.name(), dto.backend());

        participantRepository.save(participant);
    }

}
