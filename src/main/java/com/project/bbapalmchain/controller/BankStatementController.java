package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.BankStatementDTO;
import com.project.bbapalmchain.model.BankStatement;
import com.project.bbapalmchain.service.BankStatementService;
import com.project.bbapalmchain.service.CsvUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/bank-statements", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BankStatementController {

    private final CsvUploadService csvUploadService;
    private final BankStatementService bankStatementService;

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadBankStatement(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
        }

        try {
            csvUploadService.processCsvFile(file);
            return ResponseEntity.status(HttpStatus.OK).body("File processed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<BankStatementDTO>> getAll(@RequestParam String startDate,
                                                            @RequestParam String endDate,
                                                            @RequestParam Long accountId) {
        List<BankStatementDTO> result = bankStatementService.getAll(accountId, startDate, endDate);
        return ResponseEntity.ok(result);
    }
}
