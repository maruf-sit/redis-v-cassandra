package com.convay.comparison;

import java.util.List;

public record RegistrationResponse(List<ParticipantInfo> participants, long totalCount) {
}
