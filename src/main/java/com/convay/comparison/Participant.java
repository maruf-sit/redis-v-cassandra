package com.convay.comparison;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import java.time.LocalDateTime;
import java.util.UUID;

@RedisHash(value = "participant", timeToLive = 3600)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @Id
    private String id; // Format: roomId:participantId

    @Indexed
    private String roomId;

    private UUID participantId;

    private String name;

    private LocalDateTime joinedAt;

    private boolean isHost;

    private String connectedBackendName;

    public Participant(String roomId, UUID participantId, String name, String connectedBackendName) {
        this.id = roomId + ":" + participantId.toString();
        this.roomId = roomId;
        this.participantId = participantId;
        this.name = name;
        this.joinedAt = LocalDateTime.now();
        this.isHost = false;
        this.connectedBackendName = connectedBackendName;
    }
}
