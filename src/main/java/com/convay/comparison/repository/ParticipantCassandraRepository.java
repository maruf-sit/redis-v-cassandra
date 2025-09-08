package com.convay.comparison.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.convay.comparison.model.ParticipantCassandra;

import java.util.UUID;
import java.util.List;

@Repository
public interface ParticipantCassandraRepository extends CassandraRepository<ParticipantCassandra, UUID> {
    
    @Query("SELECT * FROM participants WHERE room_id = :roomId LIMIT 100")
    List<ParticipantCassandra> findByRoomId(@Param("roomId") String roomId);

    @Query("SELECT COUNT(*) FROM participants WHERE room_id = :roomId")
    Long countByRoomId(@Param("roomId") String roomId);
    
    @Query("DELETE FROM participants WHERE room_id = :roomId AND participant_id = :participantId")
    void removeParticipant(@Param("roomId") String roomId, @Param("participantId") UUID participantId);
}
