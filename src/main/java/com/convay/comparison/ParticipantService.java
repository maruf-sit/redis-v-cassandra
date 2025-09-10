package com.convay.comparison;

import java.util.List;
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
    public RegistrationResponse addParticipant(ParticipantDTO dto) {
        try {
            Participant participant = new Participant(dto.roomId(), UUID.randomUUID(), dto.name());

            participantRepository.save(participant);

            List<Participant> participants = participantRepository.fetch100ParticipantsByRoomId(dto.roomId());
            long totalCount = participantRepository.countByRoomId(dto.roomId());

            List<ParticipantInfo> participantInfoList = participants.stream()
                .map(p -> new ParticipantInfo(p.getParticipantId().toString(), p.getName(), p.getIsHost(), p.getJoinedAt()))
                .toList();
    
            return new RegistrationResponse(participantInfoList, totalCount);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid room ID");
        }
    }

}
