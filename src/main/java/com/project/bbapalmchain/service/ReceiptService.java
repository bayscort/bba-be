package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.ReceiptBatchDTO;
import com.project.bbapalmchain.dto.ReceiptDTO;
import com.project.bbapalmchain.dto.ReceiptReqDTO;
import com.project.bbapalmchain.mapper.ReceiptMapper;
import com.project.bbapalmchain.model.Account;
import com.project.bbapalmchain.model.Expenditure;
import com.project.bbapalmchain.model.FinanceItem;
import com.project.bbapalmchain.model.Receipt;
import com.project.bbapalmchain.repository.AccountRepository;
import com.project.bbapalmchain.repository.FinanceItemRepository;
import com.project.bbapalmchain.repository.ReceiptRepository;
import com.project.bbapalmchain.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final AccountRepository accountRepository;

    private final ReceiptMapper receiptMapper;
    private final FinanceItemRepository financeItemRepository;

    public String createBulk(List<ReceiptReqDTO> receiptReqDTOS) {
        receiptReqDTOS.forEach(this::create);
        return "ok";
    }

    public Long create(ReceiptReqDTO dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + dto.getAccountId()));

        FinanceItem financeItem = financeItemRepository.findById(dto.getFinanceItemId())
                .orElseThrow(() -> new NotFoundException("Finance item not found with id: " + dto.getFinanceItemId()));

        BigDecimal newBalance = account.getBalance().add(dto.getAmount());
        account.setBalance(newBalance);

        Receipt entity = receiptMapper.toEntity(dto);
        entity.setAccount(account);
        entity.setFinanceItem(financeItem);
        accountRepository.save(account);
        return receiptRepository.save(entity).getId();
    }

    public Long update(Long id, ReceiptReqDTO dto) {

        Receipt entity = receiptRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + dto.getAccountId()));

        FinanceItem financeItem = financeItemRepository.findById(dto.getFinanceItemId())
                .orElseThrow(() -> new NotFoundException("Finance item not found with id: " + dto.getFinanceItemId()));

        BigDecimal newBalance = account.getBalance().subtract(entity.getAmount()).add(dto.getAmount());
        account.setBalance(newBalance);

        entity.setReceiptDate(dto.getReceiptDate());
        entity.setAmount(dto.getAmount());
        entity.setAccount(account);
        entity.setFinanceItem(financeItem);
        entity.setNote(dto.getNote());
        accountRepository.save(account);
        return receiptRepository.save(entity).getId();
    }



    @Transactional(readOnly = true)
    public List<ReceiptDTO> getAll(Long accountId, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = (startDate != null && !startDate.isBlank())
                ? LocalDate.parse(startDate, formatter)
                : null;
        LocalDate end = (endDate != null && !endDate.isBlank())
                ? LocalDate.parse(endDate, formatter)
                : null;

        LocalDate safeStart = (start != null) ? start : LocalDate.of(1900, 1, 1);
        LocalDate safeEnd = (end != null) ? end : LocalDate.of(3000, 12, 31);

        List<Receipt> receiptList;

        if (accountId == null) {
            receiptList = receiptRepository.findReceiptByReceiptDateBetween(
                    Sort.by(Sort.Direction.DESC, "receiptDate"),
                    safeStart,
                    safeEnd
            );
        } else {
            receiptList = receiptRepository.findReceiptByAccountIdAndReceiptDateBetween(
                    Sort.by(Sort.Direction.DESC, "receiptDate"),
                    accountId,
                    safeStart,
                    safeEnd
            );
        }

        return receiptList.stream()
                .map(receiptMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReceiptBatchDTO> getAllGrouped(Long accountId, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = (startDate != null && !startDate.isBlank())
                ? LocalDate.parse(startDate, formatter)
                : null;
        LocalDate end = (endDate != null && !endDate.isBlank())
                ? LocalDate.parse(endDate, formatter)
                : null;

        LocalDate safeStart = (start != null) ? start : LocalDate.of(1900, 1, 1);
        LocalDate safeEnd = (end != null) ? end : LocalDate.of(3000, 12, 31);

        List<Receipt> receiptList;

        // --- CATATAN: Pengurutan di sini masih berdasarkan receiptDate DESC ---
        if (accountId == null) {
            receiptList = receiptRepository.findReceiptByReceiptDateBetween(
                    Sort.by(Sort.Direction.DESC, "receiptDate"),
                    safeStart,
                    safeEnd
            );
        } else {
            receiptList = receiptRepository.findReceiptByAccountIdAndReceiptDateBetween(
                    Sort.by(Sort.Direction.DESC, "receiptDate"),
                    accountId,
                    safeStart,
                    safeEnd
            );
        }

        List<ReceiptDTO> dtoList = receiptList.stream()
                .map(receiptMapper::toDTO)
                .toList();

        // Group by receiptDate
        Map<LocalDate, List<ReceiptDTO>> groupedMap = dtoList.stream()
                .collect(Collectors.groupingBy(
                        ReceiptDTO::getReceiptDate,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    // 🚀 MODIFIKASI: Urutkan list di dalam grup berdasarkan createdAt
                                    list.sort(Comparator.comparing(ReceiptDTO::getCreatedAt));
                                    return list;
                                }
                        )
                ));

        // Convert to ReceiptBatchDTO
        return groupedMap.entrySet().stream()
                .map(entry -> {
                    // entry.getValue() sekarang sudah diurutkan berdasarkan createdAt
                    List<List<ReceiptDTO>> batches = partitionList(entry.getValue(), 5);
                    return new ReceiptBatchDTO(entry.getKey(), batches);
                })
                .toList();
    }

    public void delete(Long id) {
        Receipt entity = receiptRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        Account account = entity.getAccount();
        BigDecimal newBalance = account.getBalance().subtract(entity.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);
        receiptRepository.deleteById(id);
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
