package com.convay.comparison;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantInfo {
    private String participantId;
    private String participantName;
    private Boolean isHost;
    private LocalDateTime joinedAt;
}
