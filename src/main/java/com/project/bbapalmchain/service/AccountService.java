package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.AccountDTO;
import com.project.bbapalmchain.dto.AccountLedgerDTO;
import com.project.bbapalmchain.dto.AccountReqDTO;
import com.project.bbapalmchain.dto.TransactionLedgerItemDTO;
import com.project.bbapalmchain.enums.TransactionType;
import com.project.bbapalmchain.mapper.AccountMapper;
import com.project.bbapalmchain.model.*;
import com.project.bbapalmchain.repository.AccountRepository;
import com.project.bbapalmchain.repository.EstateRepository;
import com.project.bbapalmchain.repository.ExpenditureRepository;
import com.project.bbapalmchain.repository.ReceiptRepository;
import com.project.bbapalmchain.util.NotFoundException;
import com.project.bbapalmchain.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final ReceiptRepository receiptRepository;
    private final ExpenditureRepository expenditureRepository;
    private final EstateRepository estateRepository;

    private final AccountMapper accountMapper;

    private final UserContext userContext;


    @Transactional(readOnly = true)
    public List<AccountDTO> findAll() {
        User user = userContext.getCurrentUser();
        List<Account> accountList = accountRepository.findActiveByEstateOrAll(user.getEstate(), Sort.by("id"));

        return accountList.stream()
                .map(accountMapper::toDTO)
                .toList();
    }


    @Transactional(readOnly = true)
    public AccountDTO get(Long id) {
        return accountRepository.findById(id)
                .map(accountMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(AccountReqDTO dto) {
        Account entity = accountMapper.toEntity(dto);
        entity.setActive(true);

        if (null!=dto.getEstateId()) {
            Estate estate = estateRepository.findById(dto.getEstateId()).orElseThrow(() -> new RuntimeException("Estate not found"));
            entity.setEstate(estate);
        } else {
            entity.setEstate(null);
        }
        return accountRepository.save(entity).getId();
    }

    public void update(Long id, AccountReqDTO dto) {
        Account entity = accountRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        accountMapper.toUpdate(entity, dto);

        if (null!=dto.getEstateId()) {
            Estate estate = estateRepository.findById(dto.getEstateId()).orElseThrow(() -> new RuntimeException("Estate not found"));
            entity.setEstate(estate);
        } else {
            entity.setEstate(null);
        }

        accountRepository.save(entity);
    }

    public void delete(Long id) {
        Account entity = accountRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity.setActive(false);
        accountRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public AccountLedgerDTO getAccountLedger(Long accountId, String startDate, String endDate) {

        if (startDate == null || startDate.isBlank() || endDate == null || endDate.isBlank()) {
            throw new IllegalArgumentException("Start date and end date parameters are required.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal receiptsBefore = receiptRepository.sumAmountByAccountIdAndDateBefore(accountId, start);
        BigDecimal expendituresBefore = expenditureRepository.sumAmountByAccountIdAndDateBefore(accountId, start);
        BigDecimal openingBalance = receiptsBefore.subtract(expendituresBefore);

        List<Receipt> receipts = receiptRepository.findByAccountIdAndReceiptDateBetween(accountId, start, end);
        List<Expenditure> expenditures = expenditureRepository.findByAccountIdAndExpenditureDateBetween(accountId, start, end);

        List<TransactionLedgerItemDTO> combinedTransactions = new ArrayList<>();

        receipts.forEach(receipt -> {
            TransactionLedgerItemDTO item = new TransactionLedgerItemDTO();
            item.setCreatedAt(receipt.getCreatedAt());
            item.setDate(receipt.getReceiptDate());
            item.setDescription(receipt.getFinanceItem().getName());
            item.setCredit(receipt.getAmount());
            item.setDebit(BigDecimal.ZERO);
            item.setType(TransactionType.RECEIPT);
            item.setNote(receipt.getNote());
            combinedTransactions.add(item);
        });

        expenditures.forEach(expenditure -> {
            TransactionLedgerItemDTO item = new TransactionLedgerItemDTO();
            item.setCreatedAt(expenditure.getCreatedAt());
            item.setDate(expenditure.getExpenditureDate());
            item.setDescription(expenditure.getFinanceItem().getName());
            item.setDebit(expenditure.getAmount());
            item.setCredit(BigDecimal.ZERO);
            item.setType(TransactionType.EXPENDITURE);
            item.setNote(expenditure.getNote());
            combinedTransactions.add(item);
        });

        combinedTransactions.sort(
                Comparator.comparing(TransactionLedgerItemDTO::getDate)
                        .thenComparing(item -> item.getType() == TransactionType.RECEIPT ? 0 : 1) // Receipt dulu
                        .thenComparing(TransactionLedgerItemDTO::getCreatedAt)
        );

        BigDecimal runningBalance = openingBalance;
        for (TransactionLedgerItemDTO item : combinedTransactions) {
            runningBalance = runningBalance.add(item.getCredit()).subtract(item.getDebit());
            item.setRunningBalance(runningBalance);
        }

        AccountLedgerDTO ledger = new AccountLedgerDTO();
        ledger.setAccountId(account.getId());
        ledger.setAccountName(account.getName());
        ledger.setOpeningBalance(openingBalance);
        ledger.setTransactions(combinedTransactions);
        ledger.setFinalBalance(runningBalance);

        return ledger;
    }

}
