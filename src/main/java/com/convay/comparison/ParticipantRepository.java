package com.convay.comparison;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends CrudRepository<Participant, String> {

    List<Participant> findByRoomId(String roomId);

    public record RegistrationDataDTO(List<Participant> participants, long totalCount) {
    }

    default RegistrationDataDTO getRecentParticipantsData(String roomId) {
        List<Participant> participants = findByRoomId(roomId);
        return new RegistrationDataDTO(participants.stream().limit(100).toList(), participants.size());
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
