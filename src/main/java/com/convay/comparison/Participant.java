package com.convay.comparison;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @PrimaryKeyColumn(name = "room_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String roomId;

    @PrimaryKeyColumn(name = "participant_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID participantId;

    @Column("name")
    private String name;

    @Column("joined_at")
    private LocalDateTime joinedAt;

    @Column("is_host")
    private Boolean isHost;

    public Participant(String roomId, UUID participantId, String name) {
        this.roomId = roomId;
        this.participantId = participantId;
        this.name = name;
        this.joinedAt = LocalDateTime.now();
        this.isHost = false;
    }
}
