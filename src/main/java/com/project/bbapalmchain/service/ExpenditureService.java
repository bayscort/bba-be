package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.*;
import com.project.bbapalmchain.mapper.ExpenditureMapper;
import com.project.bbapalmchain.model.*;
import com.project.bbapalmchain.repository.AccountRepository;
import com.project.bbapalmchain.repository.DriverRepository;
import com.project.bbapalmchain.repository.ExpenditureRepository;
import com.project.bbapalmchain.repository.FinanceItemRepository;
import com.project.bbapalmchain.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.function.Exp;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    private final AccountRepository accountRepository;
    private final FinanceItemRepository financeItemRepository;

    private final ExpenditureMapper expenditureMapper;
    private final DriverRepository driverRepository;

    public String createBulk(List<ExpenditureReqDTO> expenditureReqDTOList) {
        expenditureReqDTOList.forEach(this::create);
        return "ok";
    }

    public Long create(ExpenditureReqDTO dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + dto.getAccountId()));

        FinanceItem financeItem = financeItemRepository.findById(dto.getFinanceItemId())
                .orElseThrow(() -> new NotFoundException("Finance item not found with id: " + dto.getFinanceItemId()));

        BigDecimal newBalance = account.getBalance().subtract(dto.getAmount());
        account.setBalance(newBalance);

        Expenditure entity = expenditureMapper.toEntity(dto);
        entity.setAccount(account);
        entity.setFinanceItem(financeItem);
        accountRepository.save(account);
        return expenditureRepository.save(entity).getId();
    }

    public Long update(Long id, ExpenditureReqDTO dto) {

        Expenditure entity = expenditureRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + dto.getAccountId()));

        FinanceItem financeItem = financeItemRepository.findById(dto.getFinanceItemId())
                .orElseThrow(() -> new NotFoundException("Finance item not found with id: " + dto.getFinanceItemId()));

        BigDecimal newBalance = account.getBalance().add(entity.getAmount()).subtract(dto.getAmount());
        account.setBalance(newBalance);

        entity.setExpenditureDate(dto.getExpenditureDate());
        entity.setAmount(dto.getAmount());
        entity.setAccount(account);
        entity.setFinanceItem(financeItem);
        entity.setNote(dto.getNote());
        accountRepository.save(account);
        return expenditureRepository.save(entity).getId();
    }

    @Transactional(readOnly = true)
    public List<ExpenditureDTO> getAll(Long accountId, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = (startDate != null && !startDate.isBlank())
                ? LocalDate.parse(startDate, formatter)
                : null;
        LocalDate end = (endDate != null && !endDate.isBlank())
                ? LocalDate.parse(endDate, formatter)
                : null;

        LocalDate safeStart = (start != null) ? start : LocalDate.of(1900, 1, 1);
        LocalDate safeEnd = (end != null) ? end : LocalDate.of(3000, 12, 31);

        List<Expenditure> expenditureList;

        if (accountId == null) {
            expenditureList = expenditureRepository.findExpenditureByExpenditureDateBetween(
                    Sort.by(Sort.Direction.DESC, "expenditureDate"),
                    safeStart,
                    safeEnd
            );
        } else {
            expenditureList = expenditureRepository.findExpenditureByAccountIdAndExpenditureDateBetween(
                    Sort.by(Sort.Direction.DESC, "expenditureDate"),
                    accountId,
                    safeStart,
                    safeEnd
            );
        }

        return expenditureList.stream()
                .map(expenditureMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExpenditureBatchDTO> getAllGrouped(Long accountId, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = (startDate != null && !startDate.isBlank())
                ? LocalDate.parse(startDate, formatter)
                : null;
        LocalDate end = (endDate != null && !endDate.isBlank())
                ? LocalDate.parse(endDate, formatter)
                : null;

        LocalDate safeStart = (start != null) ? start : LocalDate.of(1900, 1, 1);
        LocalDate safeEnd = (end != null) ? end : LocalDate.of(3000, 12, 31);

        List<Expenditure> expenditureList;

        if (accountId == null) {
            expenditureList = expenditureRepository.findExpenditureByExpenditureDateBetween(
                    Sort.by(Sort.Direction.DESC, "expenditureDate"),
                    safeStart,
                    safeEnd
            );
        } else {
            expenditureList = expenditureRepository.findExpenditureByAccountIdAndExpenditureDateBetween(
                    Sort.by(Sort.Direction.DESC, "expenditureDate"),
                    accountId,
                    safeStart,
                    safeEnd
            );
        }

        List<ExpenditureDTO> dtoList = expenditureList.stream()
                .map(expenditureMapper::toDTO)
                .toList();

        // Group by expenditureDate
        Map<LocalDate, List<ExpenditureDTO>> groupedMap = dtoList.stream()
                .collect(Collectors.groupingBy(
                        ExpenditureDTO::getExpenditureDate,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    // 🚀 MODIFIKASI: Urutkan list di dalam grup berdasarkan createdAt (paling awal duluan)
                                    list.sort(Comparator.comparing(ExpenditureDTO::getCreatedAt));
                                    return list;
                                }
                        )
                ));

        // Convert to ExpenditureBatchDTO
        return groupedMap.entrySet().stream()
                .map(entry -> {
                    // entry.getValue() sekarang sudah diurutkan berdasarkan createdAt
                    List<List<ExpenditureDTO>> batches = partitionList(entry.getValue(), 5);
                    return new ExpenditureBatchDTO(entry.getKey(), batches);
                })
                .toList();
    }

    public void delete(Long id) {
        Expenditure entity = expenditureRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        Account account = entity.getAccount();
        BigDecimal newBalance = account.getBalance().add(entity.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);
        expenditureRepository.deleteById(id);
    }

    /**
     * Utility untuk membagi list menjadi beberapa batch dengan size max
     */
    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }


}
