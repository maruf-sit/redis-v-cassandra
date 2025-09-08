package com.convay.comparison;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@RestController
@RequestMapping("/cassandra")
public class ParticipantController {

    private final ParticipantService cassandraService;
    
    @PostMapping("/add")
    public ResponseEntity<Boolean> addParticipant(@RequestBody ParticipantDTO dto) {
        try {
            cassandraService.addParticipant(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error saving participant", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}