package com.convay.comparison;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ParticipantService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ParticipantRepository redisRepository;

    private static final String SORTED_SET_PREFIX = "room:participants:sorted:";
    private static final String COUNT_KEY_PREFIX = "room:participants:count:";
    private static final double MAX_SCORE = 12000;

    @Transactional
    public RegistrationResponse addParticipant(ParticipantDTO dto) {

        try {
            Participant participant = new Participant(dto.roomId(), UUID.randomUUID(), dto.name());

            redisRepository.save(participant);

            // Add to sorted set for efficient querying
            String sortedSetKey = SORTED_SET_PREFIX + participant.getRoomId();
            double score = (Math.random() * MAX_SCORE);

            redisTemplate.opsForZSet().add(sortedSetKey, participant.getId(), score);

            // Increment count
            String countKey = COUNT_KEY_PREFIX + participant.getRoomId();
            redisTemplate.opsForValue().increment(countKey);

            // Set TTL for cleanup
            redisTemplate.expire(sortedSetKey, Duration.ofHours(6));
            redisTemplate.expire(countKey, Duration.ofHours(6));

            return fetch100ParticipantsByRoomId(dto.roomId());
        } catch (Exception e) {
            log.error("Error adding participant: {}", e);
            return new RegistrationResponse(Collections.emptyList(), 0L);
        }
    }

    @Transactional
    public void removeParticipant(String roomId, String participantId) {
        try {
            String participantKey = roomId + ":" + participantId;

            redisRepository.deleteById(participantKey);

            String sortedSetKey = SORTED_SET_PREFIX + roomId;
            redisTemplate.opsForZSet().remove(sortedSetKey, participantKey);

            String countKey = COUNT_KEY_PREFIX + roomId;
            redisTemplate.opsForValue().decrement(countKey);

        } catch (Exception e) {
            log.error("Error removing participant: {}:{}", roomId, participantId, e);
        }
    }

    private RegistrationResponse fetch100ParticipantsByRoomId(String roomId) {
        try {
            // Get count efficiently
            String countKey = COUNT_KEY_PREFIX + roomId;
            Integer count = (Integer) redisTemplate.opsForValue().get(countKey);
            long totalCount = count != null ? count.longValue() : 0L;

            // Get recent 100 participant IDs from sorted set (most recent first)
            String sortedSetKey = SORTED_SET_PREFIX + roomId;
            Set<Object> recentIds = redisTemplate.opsForZSet()
                    .reverseRange(sortedSetKey, 0, 99);

            if (recentIds == null || recentIds.isEmpty()) {
                return new RegistrationResponse(Collections.emptyList(), totalCount);
            }

            // Batch fetch participants
            List<Participant> participants = batchFetchParticipants(recentIds);

            List<ParticipantInfo> participantInfoList = participants.stream()
                    .map(p -> new ParticipantInfo(p.getParticipantId().toString(), p.getName(), p.isHost(), p.getJoinedAt()))
                    .toList();

            return new RegistrationResponse(participantInfoList, totalCount);

        } catch (Exception e) {
            log.error("Error fetching recent participants for room: {}", roomId, e);
            return fallbackGetParticipants(roomId);
        }
    }

    private List<Participant> batchFetchParticipants(Set<Object> participantIds) {
        List<String> ids = participantIds.stream()
                .map(Object::toString)
                .toList();

        return (List<Participant>) redisRepository.findAllById(ids);
    }

    private RegistrationResponse fallbackGetParticipants(String roomId) {
        log.warn("Using fallback method for room: {}", roomId);
        return redisRepository.getRecentParticipantsData(roomId);
    }
}
