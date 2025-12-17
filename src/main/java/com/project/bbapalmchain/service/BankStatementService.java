package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.BankStatementDTO;
import com.project.bbapalmchain.mapper.BankStatementMapper;
import com.project.bbapalmchain.model.BankStatement;
import com.project.bbapalmchain.repository.BankStatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BankStatementService {

    private final BankStatementRepository bankStatementRepository;
    private final BankStatementMapper bankStatementMapper;

    public List<BankStatementDTO> getAll(Long accountId, String startDate, String endDate) {

        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);

        List<BankStatement> statements = bankStatementRepository.findByAccountIdAndPostDateBetweenOrderByPostDateAsc(accountId, startDateTime, endDateTime);

        return statements.stream()
                .map(bankStatementMapper::toDTO)
                .collect(Collectors.toList());
    }

}
