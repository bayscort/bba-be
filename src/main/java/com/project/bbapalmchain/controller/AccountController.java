package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.AccountDTO;
import com.project.bbapalmchain.dto.AccountLedgerDTO;
import com.project.bbapalmchain.dto.AccountReqDTO;
import com.project.bbapalmchain.dto.TripResponseDTO;
import com.project.bbapalmchain.service.AccountService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(accountService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody AccountReqDTO dto) {
        final Long createdId = accountService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id, @RequestBody AccountReqDTO dto) {
        accountService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ledger")
    public ResponseEntity<AccountLedgerDTO> getLedger(@RequestParam String startDate,
                                                      @RequestParam String endDate,
                                                      @RequestParam Long accountId) {
        AccountLedgerDTO result = accountService.getAccountLedger(accountId, startDate, endDate);
        return ResponseEntity.ok(result);
    }

}
