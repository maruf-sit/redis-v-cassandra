package com.convay.comparison.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.convay.comparison.model.ParticipantDTO;
import com.convay.comparison.service.ParticipantRedisService;

import lombok.Data;

@Data
@RestController
@RequestMapping("/redis")
public class ParticipantRedisController {

    private final ParticipantRedisService redisService;
    
    @PostMapping("/add")
    public ResponseEntity<Boolean> addParticipant(@RequestBody ParticipantDTO dto) {
        try {
            redisService.addParticipant(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
