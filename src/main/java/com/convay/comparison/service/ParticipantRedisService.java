package com.convay.comparison.service;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.convay.comparison.model.ParticipantDTO;
import com.convay.comparison.model.ParticipantRedis;
import com.convay.comparison.repository.ParticipantRedisRepository;
import com.convay.comparison.repository.ParticipantRedisRepository.RegistrationDataDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ParticipantRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ParticipantRedisRepository redisRepository;

    private static final String SORTED_SET_PREFIX = "room:participants:sorted:";
    private static final String COUNT_KEY_PREFIX = "room:participants:count:";

    @Transactional
    public void addParticipant(ParticipantDTO dto) {
        ParticipantRedis participant = new ParticipantRedis(dto.roomId(), UUID.randomUUID(), dto.name(), dto.backend());

        try {
            redisRepository.save(participant);

            // Add to sorted set for efficient querying
            String sortedSetKey = SORTED_SET_PREFIX + participant.getRoomId();
            double score = participant.getJoinedAt().toEpochSecond(ZoneOffset.UTC);

            redisTemplate.opsForZSet().add(sortedSetKey, participant.getId(), score);

            // Increment count
            String countKey = COUNT_KEY_PREFIX + participant.getRoomId();
            redisTemplate.opsForValue().increment(countKey);

            // Set TTL for cleanup
            redisTemplate.expire(sortedSetKey, Duration.ofHours(12));
            redisTemplate.expire(countKey, Duration.ofHours(12));
        } catch (Exception e) {
            log.error("Error adding participant: {}", participant.getId(), e);
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

    public RegistrationDataDTO getRecentParticipantsData(String roomId) {
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
                return new RegistrationDataDTO(Collections.emptyList(), totalCount);
            }

            // Batch fetch participants
            List<ParticipantRedis> participants = batchFetchParticipants(recentIds);

            return new RegistrationDataDTO(participants, totalCount);

        } catch (Exception e) {
            log.error("Error fetching recent participants for room: {}", roomId, e);
            return fallbackGetParticipants(roomId);
        }
    }

    private List<ParticipantRedis> batchFetchParticipants(Set<Object> participantIds) {
        List<String> ids = participantIds.stream()
                .map(Object::toString)
                .toList();

        return (List<ParticipantRedis>) redisRepository.findAllById(ids);
    }

    private RegistrationDataDTO fallbackGetParticipants(String roomId) {
        log.warn("Using fallback method for room: {}", roomId);
        return redisRepository.getRecentParticipantsData(roomId);
    }
}
