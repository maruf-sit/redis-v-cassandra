package com.convay.comparison.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.convay.comparison.model.ParticipantRedis;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParticipantRedisRepository extends CrudRepository<ParticipantRedis, String> {

    List<ParticipantRedis> findByRoomId(String roomId);

    public record RegistrationDataDTO(List<ParticipantRedis> participants, long totalCount) {
    }

    default RegistrationDataDTO getRecentParticipantsData(String roomId) {
        List<ParticipantRedis> participants = findByRoomId(roomId);
        return new RegistrationDataDTO(participants.stream().limit(100).toList(), participants.size());
    }

    default List<ParticipantRedis> findRecentParticipantsByRoomId(String roomId) {
        return findByRoomId(roomId).stream().limit(100).toList();
    }

    void deleteById(String id);

    default void removeParticipant(String roomId, UUID participantId) {
        String compositeId = roomId + ":" + participantId.toString();
        deleteById(compositeId);
    }
}
