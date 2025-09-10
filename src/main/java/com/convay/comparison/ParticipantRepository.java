package com.convay.comparison;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface ParticipantRepository extends CassandraRepository<Participant, UUID> {
    
    @Query("SELECT * FROM participants WHERE room_id = :roomId LIMIT 100")
    List<Participant> fetch100ParticipantsByRoomId(@Param("roomId") String roomId);

    @Query("SELECT COUNT(*) FROM participants WHERE room_id = :roomId")
    Long countByRoomId(@Param("roomId") String roomId);
    
    @Query("DELETE FROM participants WHERE room_id = :roomId AND participant_id = :participantId")
    void removeParticipant(@Param("roomId") String roomId, @Param("participantId") UUID participantId);
}
