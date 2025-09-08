package com.convay.comparison.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.convay.comparison.model.ParticipantDTO;
import com.convay.comparison.service.ParticipantCassandraService;

import lombok.Data;

@Data
@RestController
@RequestMapping("/cassandra")
public class ParticipantCassandraController {

    private final ParticipantCassandraService cassandraService;
    
    @PostMapping("/add")
    public ResponseEntity<Boolean> addParticipant(@RequestBody ParticipantDTO dto) {
        try {
            cassandraService.addParticipant(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}