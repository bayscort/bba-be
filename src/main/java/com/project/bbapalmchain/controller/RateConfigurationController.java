package com.project.bbapalmchain.controller;


import com.project.bbapalmchain.dto.MillResponseDTO;
import com.project.bbapalmchain.dto.RateConfigurationRequestDTO;
import com.project.bbapalmchain.dto.RateConfigurationResponseDTO;
import com.project.bbapalmchain.model.RateConfiguration;
import com.project.bbapalmchain.service.RateConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/rate-configurations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RateConfigurationController {

    private final RateConfigurationService rateConfigurationService;

    @PostMapping
    public ResponseEntity<RateConfigurationResponseDTO> createRate(@RequestBody RateConfigurationRequestDTO request) {
        return ResponseEntity.ok(rateConfigurationService.createRate(request));
    }

    @GetMapping("/actives")
    public ResponseEntity<List<RateConfigurationResponseDTO>> getAllActive() {
        return ResponseEntity.ok(rateConfigurationService.getAllActiveRates());
    }

    @GetMapping("/active")
    public ResponseEntity<RateConfigurationResponseDTO> getActiveRate(
            @RequestParam Long afdelingId,
            @RequestParam Long millId
    ) {
        return ResponseEntity.ok(rateConfigurationService.getActiveRate(afdelingId, millId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<RateConfigurationResponseDTO>> getHistory(
            @RequestParam Long afdelingId,
            @RequestParam Long tujuanId
    ) {
        return ResponseEntity.ok(rateConfigurationService.getHistory(afdelingId, tujuanId));
    }

}
