package com.convay.comparison;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.Column;

@Table("participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @PrimaryKeyColumn(name = "room_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String roomId;

    @PrimaryKeyColumn(name = "participant_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID participantId;

    private String name;

    @PrimaryKeyColumn(name = "joined_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private LocalDateTime joinedAt;

    @PrimaryKeyColumn(name = "left_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    private LocalDateTime leftAt;

    @PrimaryKeyColumn(name = "is_host", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    private Boolean isHost;

    @Column("connected_backend_name")
    private String backend;

    public Participant(String roomId, UUID participantId, String name, String backend) {
        this.roomId = roomId;
        this.participantId = participantId;
        this.name = name;
        this.joinedAt = LocalDateTime.now();
        this.leftAt = null;
        this.isHost = false;
        this.backend = backend;
    }
}
