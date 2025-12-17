package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.TripRequestDTO;
import com.project.bbapalmchain.dto.TripResponseDTO;
import com.project.bbapalmchain.dto.VehicleDTO;
import com.project.bbapalmchain.service.TripService;
import com.project.bbapalmchain.service.VehicleService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(value = "/api/trips", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class TripController {

    private final TripService tripService;

    @GetMapping
    public ResponseEntity<List<TripResponseDTO>> getAll() {
        return ResponseEntity.ok(tripService.getAll());
    }

    @GetMapping("/find-all")
    public ResponseEntity<Page<TripResponseDTO>> findAll(Pageable pageable,
                                                         @RequestParam(required = false) String startDate,
                                                         @RequestParam(required = false) String endDate,
                                                         @RequestParam(required = false) List<Long> millIds,
                                                         @RequestParam(required = false) List<Long> afdelingIds,
                                                         @RequestParam(required = false) List<Long> driverIds,
                                                         @RequestParam(required = false) List<Long> vehicleIds,
                                                         @RequestParam(required = false) List<Long> contractorIds,
                                                         @RequestParam(required = false) List<Long> tripTypeIds,
                                                         @RequestParam(required = false) BigDecimal loadWeightMin,
                                                         @RequestParam(required = false) BigDecimal loadWeightMax,
                                                         @RequestParam(required = false) Boolean millNull,
                                                         @RequestParam(required = false) Boolean afdelingNull,
                                                         @RequestParam(required = false) Boolean driverNull,
                                                         @RequestParam(required = false) Boolean vehicleNull,
                                                         @RequestParam(required = false) Boolean contractorNull,
                                                         @RequestParam(required = false) Boolean tripTypeNull) {

        Page<TripResponseDTO> result = tripService.findAll(
                pageable, startDate, endDate,
                millIds, afdelingIds, driverIds, vehicleIds, contractorIds, tripTypeIds,
                loadWeightMin, loadWeightMax,
                millNull, afdelingNull, driverNull, vehicleNull, contractorNull, tripTypeNull
        );
        return ResponseEntity.ok(result);
    }


    @GetMapping("/export")
    public ResponseEntity<Resource> exportTrips(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) List<Long> millIds,
            @RequestParam(required = false) List<Long> afdelingIds,
            @RequestParam(required = false) List<Long> driverIds,
            @RequestParam(required = false) List<Long> vehicleIds,
            @RequestParam(required = false) List<Long> contractorIds,
            @RequestParam(required = false) List<Long> tripTypeIds,
            @RequestParam(required = false) BigDecimal loadWeightMin,
            @RequestParam(required = false) BigDecimal loadWeightMax,
            @RequestParam(required = false) Boolean millNull,
            @RequestParam(required = false) Boolean afdelingNull,
            @RequestParam(required = false) Boolean driverNull,
            @RequestParam(required = false) Boolean vehicleNull,
            @RequestParam(required = false) Boolean contractorNull,
            @RequestParam(required = false) Boolean tripTypeNull) {

        log.info("Start exporting trips");

        byte[] excelContent = tripService.exportTripsToExcel(
                startDate, endDate,
                millIds, afdelingIds, driverIds, vehicleIds, contractorIds, tripTypeIds,
                loadWeightMin, loadWeightMax,
                millNull, afdelingNull, driverNull, vehicleNull, contractorNull, tripTypeNull
        );

        String filename = "trips_export_" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".xlsx";
        ByteArrayResource resource = new ByteArrayResource(excelContent);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(excelContent.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponseDTO> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(tripService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody TripRequestDTO dto) {
        final Long createdId = tripService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id,
                                       @RequestBody TripRequestDTO dto) {
        tripService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        tripService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
