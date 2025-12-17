package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.InternalTransactionDTO;
import com.project.bbapalmchain.dto.ManualReconciliationRequestDTO;
import com.project.bbapalmchain.dto.ReconciliationRowDTO;
import com.project.bbapalmchain.enums.ReconciliationType;
import com.project.bbapalmchain.mapper.BankStatementMapper;
import com.project.bbapalmchain.mapper.ExpenditureMapper;
import com.project.bbapalmchain.mapper.ReceiptMapper;
import com.project.bbapalmchain.model.BankStatement;
import com.project.bbapalmchain.model.Expenditure;
import com.project.bbapalmchain.model.Receipt;
import com.project.bbapalmchain.model.Reconciliation;
import com.project.bbapalmchain.repository.BankStatementRepository;
import com.project.bbapalmchain.repository.ExpenditureRepository;
import com.project.bbapalmchain.repository.ReceiptRepository;
import com.project.bbapalmchain.repository.ReconciliationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReconciliationService {

    private static final BigDecimal AMOUNT_TOLERANCE = new BigDecimal("0.00");
    private static final long DATE_TOLERANCE_DAYS = 0;

    private final BankStatementRepository bankStatementRepository;
    private final ReceiptRepository receiptRepository;
    private final ExpenditureRepository expenditureRepository;
    private final ReconciliationRepository reconciliationRepository;

    private final BankStatementMapper bankStatementMapper;

    public List<ReconciliationRowDTO> getAll(Long accountId, String startDate, String endDate) {

        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<ReconciliationRowDTO> rows = new ArrayList<>();

        // 1. Ambil semua rekonsiliasi yang relevan
        List<Reconciliation> reconciliations = reconciliationRepository
                .findByBankStatement_AccountIdAndBankStatement_PostDateBetween(accountId, startDateTime, endDateTime);

        // Buat set ID untuk melacak item mana yang sudah diproses
        Set<Long> reconciledStmtIds = new HashSet<>();
        Set<Long> reconciledReceiptIds = new HashSet<>();
        Set<Long> reconciledExpenditureIds = new HashSet<>();

        // 2. Proses item yang SUDAH direkonsiliasi
        for (Reconciliation recon : reconciliations) {
            ReconciliationRowDTO row = new ReconciliationRowDTO();
            row.setId(recon.getId());
            row.setStatus("RECONCILED");
            row.setReconciliationId(recon.getId());
            row.setDate(recon.getReconciliationDate());
            row.setBankStatement(bankStatementMapper.toDTO(recon.getBankStatement()));

            if (recon.getReceipt() != null) {
                InternalTransactionDTO internalTransactionDTO = new InternalTransactionDTO();
                internalTransactionDTO.setId(recon.getReceipt().getId());
                internalTransactionDTO.setType("RECEIPT");
                internalTransactionDTO.setDescription(recon.getReceipt().getFinanceItem().getName());
                internalTransactionDTO.setAmount(recon.getReceipt().getAmount());
                internalTransactionDTO.setDate(recon.getReceipt().getReceiptDate().atStartOfDay());
                row.setInternalTransaction(internalTransactionDTO);
                reconciledReceiptIds.add(recon.getReceipt().getId());
            }
            if (recon.getExpenditure() != null) {
                InternalTransactionDTO internalTransactionDTO = new InternalTransactionDTO();
                internalTransactionDTO.setId(recon.getExpenditure().getId());
                internalTransactionDTO.setType("EXPENDITURE");
                internalTransactionDTO.setDescription(recon.getExpenditure().getFinanceItem().getName());
                internalTransactionDTO.setAmount(recon.getExpenditure().getAmount());
                internalTransactionDTO.setDate(recon.getExpenditure().getExpenditureDate().atStartOfDay());
                row.setInternalTransaction(internalTransactionDTO);
                reconciledExpenditureIds.add(recon.getExpenditure().getId());
            }
            rows.add(row);
            reconciledStmtIds.add(recon.getBankStatement().getId());
        }

        // 3. Proses item Bank Statement yang BELUM direkonsiliasi
        List<BankStatement> unreconciledStmts = bankStatementRepository
                .findByAccountIdAndPostDateBetweenAndIdNotIn(accountId, startDateTime, endDateTime,
                        reconciledStmtIds.isEmpty() ? List.of(-1L) : reconciledStmtIds);

        for (BankStatement stmt : unreconciledStmts) {
            ReconciliationRowDTO row = new ReconciliationRowDTO();
            row.setStatus("UNRECONCILED_BANK");
            row.setDate(stmt.getPostDate());
            row.setBankStatement(bankStatementMapper.toDTO(stmt));
            rows.add(row);
        }

        List<Receipt> unreconciledReceipts = receiptRepository
                .findByAccountIdAndReceiptDateBetweenAndIdNotIn(accountId, start, end,
                        reconciledReceiptIds.isEmpty() ? List.of(-1L) : reconciledReceiptIds);

        for (Receipt receipt : unreconciledReceipts) {
            ReconciliationRowDTO row = new ReconciliationRowDTO();
            row.setStatus("UNRECONCILED_INTERNAL");
            row.setDate(receipt.getReceiptDate().atStartOfDay());
            InternalTransactionDTO internalTransactionDTO = new InternalTransactionDTO();
            internalTransactionDTO.setId(receipt.getId());
            internalTransactionDTO.setType("RECEIPT");
            internalTransactionDTO.setDescription(receipt.getFinanceItem().getName());
            internalTransactionDTO.setAmount(receipt.getAmount());
            internalTransactionDTO.setDate(receipt.getReceiptDate().atStartOfDay());
            row.setInternalTransaction(internalTransactionDTO);
            // bankStatement akan null
            rows.add(row);
        }

        List<Expenditure> unreconciledExpenditures = expenditureRepository
                .findByAccountIdAndExpenditureDateBetweenAndIdNotIn(accountId, start, end,
                        reconciledExpenditureIds.isEmpty() ? List.of(-1L) : reconciledExpenditureIds);

        for (Expenditure exp : unreconciledExpenditures) {
            ReconciliationRowDTO row = new ReconciliationRowDTO();
            row.setStatus("UNRECONCILED_INTERNAL");
            row.setDate(exp.getExpenditureDate().atStartOfDay());
            InternalTransactionDTO internalTransactionDTO = new InternalTransactionDTO();
            internalTransactionDTO.setId(exp.getId());
            internalTransactionDTO.setType("EXPENDITURE");
            internalTransactionDTO.setDescription(exp.getFinanceItem().getName());
            internalTransactionDTO.setAmount(exp.getAmount());
            internalTransactionDTO.setDate(exp.getExpenditureDate().atStartOfDay());
            row.setInternalTransaction(internalTransactionDTO);
            // bankStatement akan null
            rows.add(row);
        }

        // 5. Langkah Terakhir: Urutkan semua baris berdasarkan tanggal
        rows.sort(Comparator.comparing(ReconciliationRowDTO::getDate));

        return rows;
    }

    public void manualReconcile(ManualReconciliationRequestDTO request) {

        if (request.getReceiptId() != null && request.getExpenditureId() != null) {
            throw new IllegalArgumentException("Cannot reconcile with both a receipt and an expenditure.");
        }

        BankStatement stmt = bankStatementRepository.findById(request.getBankStatementId())
                .orElseThrow(() -> new EntityNotFoundException("Bank Statement not found"));

        // Toleransi nominal (misalnya Rp 10.00)
        BigDecimal tolerance = new BigDecimal("10.00");

        // Rentang toleransi tanggal (misalnya 3 hari)
        long dateToleranceDays = 2;

        Reconciliation recon = new Reconciliation();
        recon.setBankStatement(stmt);

        if (request.getReceiptId() != null) {
            Receipt receipt = receiptRepository.findById(request.getReceiptId())
                    .orElseThrow(() -> new EntityNotFoundException("Receipt not found"));

            // Validasi tipe: Receipt harus credit
            if (stmt.getCreditAmount() == null || stmt.getCreditAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Receipt can only be reconciled with a Bank Statement having credit amount > 0.");
            }

            // Validasi amount
            BigDecimal difference = stmt.getCreditAmount().subtract(receipt.getAmount()).abs();
            if (difference.compareTo(tolerance) > 0) {
                throw new IllegalArgumentException(
                        String.format("Amount mismatch. Difference %s exceeds allowed tolerance %s", difference, tolerance)
                );
            }

            // Validasi tanggal
            long daysBetween = Math.abs(ChronoUnit.DAYS.between(stmt.getPostDate(), receipt.getReceiptDate().atStartOfDay()));
            if (daysBetween > dateToleranceDays) {
                throw new IllegalArgumentException(
                        String.format("Date difference %d days exceeds allowed tolerance %d days", daysBetween, dateToleranceDays)
                );
            }

            recon.setReceipt(receipt);
        }

        if (request.getExpenditureId() != null) {
            Expenditure expenditure = expenditureRepository.findById(request.getExpenditureId())
                    .orElseThrow(() -> new EntityNotFoundException("Expenditure not found"));

            // Validasi tipe: Expenditure harus debit
            if (stmt.getDebitAmount() == null || stmt.getDebitAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Expenditure can only be reconciled with a Bank Statement having debit amount > 0.");
            }

            // Validasi amount
            BigDecimal difference = stmt.getDebitAmount().subtract(expenditure.getAmount()).abs();
            if (difference.compareTo(tolerance) > 0) {
                throw new IllegalArgumentException(
                        String.format("Amount mismatch. Difference %s exceeds allowed tolerance %s", difference, tolerance)
                );
            }

            // Validasi tanggal
            long daysBetween = Math.abs(ChronoUnit.DAYS.between(stmt.getPostDate(), expenditure.getExpenditureDate().atStartOfDay()));
            if (daysBetween > dateToleranceDays) {
                throw new IllegalArgumentException(
                        String.format("Date difference %d days exceeds allowed tolerance %d days", daysBetween, dateToleranceDays)
                );
            }

            recon.setExpenditure(expenditure);
        }

        recon.setReconciliationDate(LocalDateTime.now());
        recon.setReconciliationType(ReconciliationType.MANUAL);
        reconciliationRepository.save(recon);
    }

    public String autoReconcile(Long accountId, String startDate, String endDate) {
        log.info("Starting auto-reconciliation for accountId: {}, from: {} to: {}", accountId, startDate, endDate);

        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<Reconciliation> existingReconciliations = reconciliationRepository
                .findByBankStatement_AccountIdAndBankStatement_PostDateBetween(accountId, startDateTime, endDateTime);

        Set<Long> reconciledStmtIds = existingReconciliations.stream()
                .map(r -> r.getBankStatement().getId())
                .collect(Collectors.toSet());
        Set<Long> reconciledReceiptIds = existingReconciliations.stream()
                .filter(r -> r.getReceipt() != null)
                .map(r -> r.getReceipt().getId())
                .collect(Collectors.toSet());
        Set<Long> reconciledExpenditureIds = existingReconciliations.stream()
                .filter(r -> r.getExpenditure() != null)
                .map(r -> r.getExpenditure().getId())
                .collect(Collectors.toSet());

        List<BankStatement> unreconciledStmts = bankStatementRepository
                .findByAccountIdAndPostDateBetweenAndIdNotIn(accountId, startDateTime, endDateTime,
                        reconciledStmtIds.isEmpty() ? Set.of(-1L) : reconciledStmtIds);

        List<Receipt> unreconciledReceipts = receiptRepository
                .findByAccountIdAndReceiptDateBetweenAndIdNotIn(accountId, start, end,
                        reconciledReceiptIds.isEmpty() ? Set.of(-1L) : reconciledReceiptIds);

        List<Expenditure> unreconciledExpenditures = expenditureRepository
                .findByAccountIdAndExpenditureDateBetweenAndIdNotIn(accountId, start, end,
                        reconciledExpenditureIds.isEmpty() ? Set.of(-1L) : reconciledExpenditureIds);

        log.info("Found {} unreconciled bank statements, {} receipts, and {} expenditures.",
                unreconciledStmts.size(), unreconciledReceipts.size(), unreconciledExpenditures.size());

        List<Reconciliation> newReconciliations = new ArrayList<>();
        int reconciledCount = 0;

        Set<Long> matchedReceiptIds = new HashSet<>();
        Set<Long> matchedExpenditureIds = new HashSet<>();

        for (BankStatement stmt : unreconciledStmts) {

            if (stmt.getCreditAmount() != null && stmt.getCreditAmount().compareTo(BigDecimal.ZERO) > 0) {
                List<Receipt> potentialMatches = new ArrayList<>();
                for (Receipt receipt : unreconciledReceipts) {
                    if (matchedReceiptIds.contains(receipt.getId())) continue;

                    boolean amountMatch = stmt.getCreditAmount().subtract(receipt.getAmount()).abs().compareTo(AMOUNT_TOLERANCE) <= 0;
                    long daysBetween = Math.abs(ChronoUnit.DAYS.between(stmt.getPostDate().toLocalDate(), receipt.getReceiptDate()));
                    boolean dateMatch = daysBetween <= DATE_TOLERANCE_DAYS;

                    if (amountMatch && dateMatch) {
                        potentialMatches.add(receipt);
                    }
                }

                if (potentialMatches.size() == 1) {
                    Receipt matchedReceipt = potentialMatches.get(0);
                    newReconciliations.add(createReconciliation(stmt, matchedReceipt, null));
                    matchedReceiptIds.add(matchedReceipt.getId());
                    reconciledCount++;
                } else if (potentialMatches.size() > 1) {
                    log.warn("Skipping bank statement {} due to ambiguous match with {} potential receipts.", stmt.getId(), potentialMatches.size());
                }
            }
            else if (stmt.getDebitAmount() != null && stmt.getDebitAmount().compareTo(BigDecimal.ZERO) > 0) {
                List<Expenditure> potentialMatches = new ArrayList<>();
                for (Expenditure exp : unreconciledExpenditures) {
                    if (matchedExpenditureIds.contains(exp.getId())) continue;

                    boolean amountMatch = stmt.getDebitAmount().subtract(exp.getAmount()).abs().compareTo(AMOUNT_TOLERANCE) <= 0;
                    long daysBetween = Math.abs(ChronoUnit.DAYS.between(stmt.getPostDate().toLocalDate(), exp.getExpenditureDate()));
                    boolean dateMatch = daysBetween <= DATE_TOLERANCE_DAYS;

                    if (amountMatch && dateMatch) {
                        potentialMatches.add(exp);
                    }
                }

                if (potentialMatches.size() == 1) {
                    Expenditure matchedExpenditure = potentialMatches.get(0);
                    newReconciliations.add(createReconciliation(stmt, null, matchedExpenditure));
                    matchedExpenditureIds.add(matchedExpenditure.getId());
                    reconciledCount++;
                } else if (potentialMatches.size() > 1) {
                    log.warn("Skipping bank statement {} due to ambiguous match with {} potential expenditures.", stmt.getId(), potentialMatches.size());
                }
            }
        }

        if (!newReconciliations.isEmpty()) {
            reconciliationRepository.saveAll(newReconciliations);
            log.info("Saved {} new reconciliation records.", newReconciliations.size());
        }

        return String.format("Auto-reconciliation complete. %d transactions were successfully reconciled.", reconciledCount);
    }

//    public String autoReconcile(Long accountId, String startDate, String endDate) {
//        log.info("Starting auto-reconciliation for accountId: {}, from: {} to: {}", accountId, startDate, endDate);
//
//        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
//        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);
//        LocalDate start = LocalDate.parse(startDate);
//        LocalDate end = LocalDate.parse(endDate);
//
//        // Get existing reconciliations
//        List<Reconciliation> existingReconciliations = reconciliationRepository
//                .findByBankStatement_AccountIdAndBankStatement_PostDateBetween(accountId, startDateTime, endDateTime);
//
//        Set<Long> reconciledStmtIds = existingReconciliations.stream()
//                .map(r -> r.getBankStatement().getId())
//                .collect(Collectors.toSet());
//        Set<Long> reconciledReceiptIds = existingReconciliations.stream()
//                .filter(r -> r.getReceipt() != null)
//                .map(r -> r.getReceipt().getId())
//                .collect(Collectors.toSet());
//        Set<Long> reconciledExpenditureIds = existingReconciliations.stream()
//                .filter(r -> r.getExpenditure() != null)
//                .map(r -> r.getExpenditure().getId())
//                .collect(Collectors.toSet());
//
//        // Get unreconciled data
//        List<BankStatement> unreconciledStmts = bankStatementRepository
//                .findByAccountIdAndPostDateBetweenAndIdNotIn(accountId, startDateTime, endDateTime,
//                        reconciledStmtIds.isEmpty() ? Set.of(-1L) : reconciledStmtIds);
//
//        List<Receipt> unreconciledReceipts = receiptRepository
//                .findByAccountIdAndReceiptDateBetweenAndIdNotIn(accountId, start, end,
//                        reconciledReceiptIds.isEmpty() ? Set.of(-1L) : reconciledReceiptIds);
//
//        List<Expenditure> unreconciledExpenditures = expenditureRepository
//                .findByAccountIdAndExpenditureDateBetweenAndIdNotIn(accountId, start, end,
//                        reconciledExpenditureIds.isEmpty() ? Set.of(-1L) : reconciledExpenditureIds);
//
//        log.info("Found {} unreconciled bank statements, {} receipts, and {} expenditures.",
//                unreconciledStmts.size(), unreconciledReceipts.size(), unreconciledExpenditures.size());
//
//        List<Reconciliation> newReconciliations = new ArrayList<>();
//
//        // Improved matching algorithm
//        newReconciliations.addAll(matchCreditsWithReceipts(unreconciledStmts, unreconciledReceipts));
//        newReconciliations.addAll(matchDebitsWithExpenditures(unreconciledStmts, unreconciledExpenditures));
//
//        if (!newReconciliations.isEmpty()) {
//            reconciliationRepository.saveAll(newReconciliations);
//            log.info("Saved {} new reconciliation records.", newReconciliations.size());
//        }
//
//        return String.format("Auto-reconciliation complete. %d transactions were successfully reconciled.",
//                newReconciliations.size());
//    }
//
//    // Match credits with receipts using optimal pairing
//    private List<Reconciliation> matchCreditsWithReceipts(List<BankStatement> statements, List<Receipt> receipts) {
//        List<Reconciliation> reconciliations = new ArrayList<>();
//
//        // Filter credit statements
//        List<BankStatement> creditStatements = statements.stream()
//                .filter(stmt -> stmt.getCreditAmount() != null && stmt.getCreditAmount().compareTo(BigDecimal.ZERO) > 0)
//                .collect(Collectors.toList());
//
//        // Create match candidates with scores
//        List<MatchCandidate> candidates = new ArrayList<>();
//
//        for (BankStatement stmt : creditStatements) {
//            for (Receipt receipt : receipts) {
//                MatchScore score = calculateMatchScore(stmt, receipt);
//                if (score.isValidMatch()) {
//                    candidates.add(new MatchCandidate(stmt, receipt, null, score));
//                }
//            }
//        }
//
//        // Sort by match quality (exact matches first, then by date proximity, then by amount proximity)
//        candidates.sort((a, b) -> b.getScore().compareTo(a.getScore()));
//
//        // Select best matches without conflicts
//        Set<Long> usedStatements = new HashSet<>();
//        Set<Long> usedReceipts = new HashSet<>();
//
//        for (MatchCandidate candidate : candidates) {
//            Long stmtId = candidate.getStatement().getId();
//            Long receiptId = candidate.getReceipt().getId();
//
//            if (!usedStatements.contains(stmtId) && !usedReceipts.contains(receiptId)) {
//                reconciliations.add(createReconciliation(candidate.getStatement(), candidate.getReceipt(), null));
//                usedStatements.add(stmtId);
//                usedReceipts.add(receiptId);
//
//                log.debug("Matched statement {} with receipt {} (score: {})",
//                        stmtId, receiptId, candidate.getScore());
//            }
//        }
//
//        return reconciliations;
//    }
//
//    // Match debits with expenditures using optimal pairing
//    private List<Reconciliation> matchDebitsWithExpenditures(List<BankStatement> statements, List<Expenditure> expenditures) {
//        List<Reconciliation> reconciliations = new ArrayList<>();
//
//        // Filter debit statements
//        List<BankStatement> debitStatements = statements.stream()
//                .filter(stmt -> stmt.getDebitAmount() != null && stmt.getDebitAmount().compareTo(BigDecimal.ZERO) > 0)
//                .collect(Collectors.toList());
//
//        // Create match candidates with scores
//        List<MatchCandidate> candidates = new ArrayList<>();
//
//        for (BankStatement stmt : debitStatements) {
//            for (Expenditure expenditure : expenditures) {
//                MatchScore score = calculateMatchScore(stmt, expenditure);
//                if (score.isValidMatch()) {
//                    candidates.add(new MatchCandidate(stmt, null, expenditure, score));
//                }
//            }
//        }
//
//        // Sort by match quality
//        candidates.sort((a, b) -> b.getScore().compareTo(a.getScore()));
//
//        // Select best matches without conflicts
//        Set<Long> usedStatements = new HashSet<>();
//        Set<Long> usedExpenditures = new HashSet<>();
//
//        for (MatchCandidate candidate : candidates) {
//            Long stmtId = candidate.getStatement().getId();
//            Long expenditureId = candidate.getExpenditure().getId();
//
//            if (!usedStatements.contains(stmtId) && !usedExpenditures.contains(expenditureId)) {
//                reconciliations.add(createReconciliation(candidate.getStatement(), null, candidate.getExpenditure()));
//                usedStatements.add(stmtId);
//                usedExpenditures.add(expenditureId);
//
//                log.debug("Matched statement {} with expenditure {} (score: {})",
//                        stmtId, expenditureId, candidate.getScore());
//            }
//        }
//
//        return reconciliations;
//    }
//
//    // Calculate match score between bank statement and receipt
//    private MatchScore calculateMatchScore(BankStatement stmt, Receipt receipt) {
//        BigDecimal amountDiff = stmt.getCreditAmount().subtract(receipt.getAmount()).abs();
//        boolean amountMatch = amountDiff.compareTo(AMOUNT_TOLERANCE) <= 0;
//
//        long daysBetween = Math.abs(ChronoUnit.DAYS.between(stmt.getPostDate().toLocalDate(), receipt.getReceiptDate()));
//        boolean dateMatch = daysBetween <= DATE_TOLERANCE_DAYS;
//
//        if (!amountMatch || !dateMatch) {
//            return MatchScore.invalid();
//        }
//
//        // Calculate score: higher is better
//        // Exact amount match gets bonus, closer dates get higher scores
//        int score = 1000;
//
//        // Amount precision bonus (exact match gets 500 points)
//        if (amountDiff.compareTo(BigDecimal.ZERO) == 0) {
//            score += 500;
//        } else {
//            // Closer amounts get higher scores
//            score += Math.max(0, 100 - (amountDiff.intValue() * 10));
//        }
//
//        // Date proximity bonus (same date gets 400 points)
//        score += Math.max(0, 400 - (daysBetween * 50));
//
//        return new MatchScore(score, amountDiff, daysBetween);
//    }
//
//    // Calculate match score between bank statement and expenditure
//    private MatchScore calculateMatchScore(BankStatement stmt, Expenditure expenditure) {
//        BigDecimal amountDiff = stmt.getDebitAmount().subtract(expenditure.getAmount()).abs();
//        boolean amountMatch = amountDiff.compareTo(AMOUNT_TOLERANCE) <= 0;
//
//        long daysBetween = Math.abs(ChronoUnit.DAYS.between(stmt.getPostDate().toLocalDate(), expenditure.getExpenditureDate()));
//        boolean dateMatch = daysBetween <= DATE_TOLERANCE_DAYS;
//
//        if (!amountMatch || !dateMatch) {
//            return MatchScore.invalid();
//        }
//
//        // Calculate score: higher is better
//        int score = 1000;
//
//        // Amount precision bonus
//        if (amountDiff.compareTo(BigDecimal.ZERO) == 0) {
//            score += 500;
//        } else {
//            score += Math.max(0, 100 - (amountDiff.intValue() * 10));
//        }
//
//        // Date proximity bonus
//        score += Math.max(0, 400 - (daysBetween * 50));
//
//        return new MatchScore(score, amountDiff, daysBetween);
//    }
//
//    // Inner helper classes - add these inside your service class
//    private static class MatchCandidate {
//        private final BankStatement statement;
//        private final Receipt receipt;
//        private final Expenditure expenditure;
//        private final MatchScore score;
//
//        public MatchCandidate(BankStatement statement, Receipt receipt, Expenditure expenditure, MatchScore score) {
//            this.statement = statement;
//            this.receipt = receipt;
//            this.expenditure = expenditure;
//            this.score = score;
//        }
//
//        // Getters
//        public BankStatement getStatement() { return statement; }
//        public Receipt getReceipt() { return receipt; }
//        public Expenditure getExpenditure() { return expenditure; }
//        public MatchScore getScore() { return score; }
//    }
//
//    private static class MatchScore implements Comparable<MatchScore> {
//        private final int score;
//        private final BigDecimal amountDiff;
//        private final long daysBetween;
//        private final boolean valid;
//
//        public MatchScore(int score, BigDecimal amountDiff, long daysBetween) {
//            this.score = score;
//            this.amountDiff = amountDiff;
//            this.daysBetween = daysBetween;
//            this.valid = true;
//        }
//
//        private MatchScore() {
//            this.score = 0;
//            this.amountDiff = BigDecimal.ZERO;
//            this.daysBetween = 0;
//            this.valid = false;
//        }
//
//        public static MatchScore invalid() {
//            return new MatchScore();
//        }
//
//        public boolean isValidMatch() {
//            return valid;
//        }
//
//        @Override
//        public int compareTo(MatchScore other) {
//            return Integer.compare(this.score, other.score);
//        }
//
//        @Override
//        public String toString() {
//            return String.format("Score: %d, AmountDiff: %s, DaysDiff: %d", score, amountDiff, daysBetween);
//        }
//    }

    public void unreconcile(Long reconciliationId) {
        log.info("Attempting to unreconcile record with id: {}", reconciliationId);

        // Cek apakah rekonsiliasi dengan ID tersebut ada
        if (!reconciliationRepository.existsById(reconciliationId)) {
            throw new EntityNotFoundException("Reconciliation with ID " + reconciliationId + " not found.");
        }

        // Jika ada, hapus recordnya
        reconciliationRepository.deleteById(reconciliationId);

        log.info("Successfully unreconciled record with id: {}", reconciliationId);
    }

    private Reconciliation createReconciliation(BankStatement stmt, Receipt receipt, Expenditure expenditure) {
        Reconciliation recon = new Reconciliation();
        recon.setBankStatement(stmt);
        recon.setReceipt(receipt);
        recon.setExpenditure(expenditure);
        recon.setReconciliationDate(LocalDateTime.now());
        recon.setReconciliationType(ReconciliationType.AUTO);
        return recon;
    }



}
