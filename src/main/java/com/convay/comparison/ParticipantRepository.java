package com.convay.comparison;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends CrudRepository<Participant, String> {

    List<Participant> findByRoomId(String roomId);

    default RegistrationResponse getRecentParticipantsData(String roomId) {
        List<Participant> participants = findByRoomId(roomId);

        long totalCount = participants.size();
        List<ParticipantInfo> participantInfoList = participants.stream()
                .map(p -> new ParticipantInfo(p.getParticipantId().toString(), p.getName(), p.isHost(), p.getJoinedAt()))
                .toList();
    
        return new RegistrationResponse(participantInfoList, totalCount);
    }

    default List<Participant> findRecentParticipantsByRoomId(String roomId) {
        return findByRoomId(roomId).stream().limit(100).toList();
    }

    void deleteById(String id);

    default void removeParticipant(String roomId, UUID participantId) {
        String compositeId = roomId + ":" + participantId.toString();
        deleteById(compositeId);
    }
}
