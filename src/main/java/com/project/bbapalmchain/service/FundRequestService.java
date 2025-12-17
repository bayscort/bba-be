package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.*;
import com.project.bbapalmchain.enums.ApprovalStage;
import com.project.bbapalmchain.mapper.FundRequestItemMapper;
import com.project.bbapalmchain.mapper.FundRequestMapper;
import com.project.bbapalmchain.model.*;
import com.project.bbapalmchain.repository.FinanceItemRepository;
import com.project.bbapalmchain.repository.FundRequestRepository;
import com.project.bbapalmchain.repository.RoleRepository;
import com.project.bbapalmchain.repository.TripRepository;
import com.project.bbapalmchain.util.ForbiddenException;
import com.project.bbapalmchain.util.NotFoundException;
import com.project.bbapalmchain.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FundRequestService {

    private static final String[] SATUAN = {
            "", "satu", "dua", "tiga", "empat", "lima", "enam", "tujuh", "delapan", "sembilan"
    };
    private static final String[] SKALA = {
            "", "ribu", "juta", "miliar", "triliun", "kuadriliun"
    };

    private static final String PREFIX = "BBA";

    private static final String[] ROMAN_MONTHS = {
            "I", "II", "III", "IV", "V", "VI",
            "VII", "VIII", "IX", "X", "XI", "XII"
    };

    private final FundRequestRepository fundRequestRepository;

    private final FundRequestMapper fundRequestMapper;
    private final FundRequestItemMapper fundRequestItemMapper;

    private final FundRequestApprovalLogService fundRequestApprovalLogService;
    private final FinanceItemRepository financeItemRepository;
    private final RoleRepository roleRepository;

    private final UserContext userContext;

    @Transactional(readOnly = true)
    public List<FundRequestRespDTO> findAll() {
        final List<FundRequest> fundRequestList = fundRequestRepository.findAll(Sort.by("id"));
        return fundRequestList.stream()
                .map(fundRequestMapper::toRespDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FundRequestRespDTO> getAll(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = (startDate != null && !startDate.isBlank())
                ? LocalDate.parse(startDate, formatter)
                : null;
        LocalDate end = (endDate != null && !endDate.isBlank())
                ? LocalDate.parse(endDate, formatter)
                : null;

        List<FundRequest> fundRequestList;

        if (start == null && end == null) {
            fundRequestList = fundRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
        } else {
            LocalDate safeStart = (start != null) ? start : LocalDate.of(1900, 1, 1);
            LocalDate safeEnd = (end != null) ? end : LocalDate.of(3000, 12, 31);

            fundRequestList = fundRequestRepository.findFundRequestByDateBetween(
                    Sort.by(Sort.Direction.DESC, "date"),
                    safeStart,
                    safeEnd
            );
        }

        return fundRequestList.stream()
                .map(fundRequestMapper::toRespDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public FundRequestRespDTO get(Long id) {
        return fundRequestRepository.findById(id)
                .map(fundRequestMapper::toRespDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(FundRequestDTO dto) {
        FundRequest entity = fundRequestMapper.toEntity(dto);

        String fundRequestCode = generateFundRequestCode();
        entity.setFundRequestCode(fundRequestCode);

        BigDecimal total = BigDecimal.ZERO;
        List<FundRequestItem> fundRequestItemList = new ArrayList<>();
        if (dto.getFundRequestItemList() != null) {
            for (FundRequestItemDTO friDTO : dto.getFundRequestItemList()) {
                FundRequestItem fri = new FundRequestItem();

                Optional<FinanceItem> financeItemOpt = financeItemRepository.findById(friDTO.getFinanceItemId());

                fri.setAmount(friDTO.getAmount());
                fri.setBankAccountNumber(friDTO.getBankAccountNumber());
                fri.setDescription(friDTO.getDescription());
                fri.setFinanceItem(financeItemOpt.orElse(null));
                fri.setFundRequest(entity);

                fundRequestItemList.add(fri);
                total = total.add(friDTO.getAmount());
            }
        }

        entity.setTotalAmount(total);
        entity.setTotalAmountInWords(toRupiahWords(total));
        entity.setFundRequestItemList(fundRequestItemList);

        entity.setNextApprovalRoleId(2L);
        Long savedEntity = fundRequestRepository.save(entity).getId();

        FundRequestApprovalLogReqDTO fundRequestApprovalLogDTO = new FundRequestApprovalLogReqDTO();
        fundRequestApprovalLogDTO.setFundRequestId(savedEntity);
        fundRequestApprovalLogDTO.setApprovalStage(ApprovalStage.SUBMITTED_BY_ADMIN_OPS);
        fundRequestApprovalLogDTO.setStageTimestamp(LocalDateTime.now());
        fundRequestApprovalLogDTO.setNotes(null);
        fundRequestApprovalLogService.create(fundRequestApprovalLogDTO);

        return savedEntity;
    }

    public void update(Long id, FundRequestDTO dto) {

        Long roleId = userContext.getCurrentUser().getRole().getId();

        FundRequest entity = fundRequestRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        final Map<String, List<Long>> allowedRolesForStage = Map.of(
                "SUBMITTED_BY_ADMIN_OPS",   List.of(2L, 4L),
                "ACKNOWLEDGED_BY_STAFF_OPS",List.of(2L, 9L),
                "APPROVED_BY_MANAGER_OPS",  List.of(9L, 8L),
                "REVIEWED_BY_FINANCE",      List.of(3L, 8L),
                "APPROVED_BY_MANAGER_FIN",  List.of(3L, 10L),
                "APPROVED_BY_DIRECTOR",     List.of(10L)
        );

        Optional<FundRequestApprovalLog> latestLogOpt = entity.getFundRequestApprovalLogList()
                .stream()
                .max(Comparator.comparing(FundRequestApprovalLog::getStageTimestamp));

        if (latestLogOpt.isEmpty()) {
            throw new IllegalStateException("Gagal update: Histori approval tidak ditemukan.");
        }

        String lastStage = String.valueOf(latestLogOpt.get().getApprovalStage());

        if ("REJECTED".equals(lastStage)) {
            throw new ForbiddenException("Request ini telah ditolak dan tidak dapat diedit.");
        }

        List<Long> allowedRoles = allowedRolesForStage.get(lastStage);

        if (allowedRoles == null || !allowedRoles.contains(roleId)) {
            throw new ForbiddenException(
                    "Anda tidak memiliki hak untuk mengedit request pada tahap: " + lastStage
            );
        }

        fundRequestMapper.toUpdate(entity, dto);

        BigDecimal total = BigDecimal.ZERO;

        Map<Long, FundRequestItem> existingFundRequestItems = entity.getFundRequestItemList().stream()
                .filter(fri -> fri.getId() != null)
                .collect(Collectors.toMap(FundRequestItem::getId, Function.identity()));

        entity.getFundRequestItemList().clear();

        for (FundRequestItemDTO friDto : dto.getFundRequestItemList()) {
            FundRequestItem fri;

            if (friDto.getId() != null && existingFundRequestItems.containsKey(friDto.getId())) {
                fri = existingFundRequestItems.get(friDto.getId());
                fundRequestItemMapper.toUpdate(fri, friDto);
            } else {
                fri = fundRequestItemMapper.toEntity(friDto);
                Optional<FinanceItem> fi = financeItemRepository.findById(friDto.getFinanceItemId());
                fri.setFinanceItem(fi.orElse(null));
            }

            fri.setFundRequest(entity);
            entity.getFundRequestItemList().add(fri);
            total = total.add(fri.getAmount());
        }

        entity.setTotalAmount(total);
        entity.setTotalAmountInWords(toRupiahWords(total));
        fundRequestRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<FundRequestRespDTO> getByApprovalRole(String roleName) {

        Optional<Role> roleOpt = roleRepository.findByName(roleName);

        if (roleOpt.isPresent()) {
            final List<FundRequest> fundRequestList = fundRequestRepository.findFundRequestByNextApprovalRoleId(roleOpt.get().getId());
            return fundRequestList.stream()
                    .map(fundRequestMapper::toRespDTO)
                    .toList();
        } else {
            throw new NotFoundException();
        }
    }

    private static String toRupiahWords(BigDecimal number) {
        if (number == null || number.compareTo(BigDecimal.ZERO) == 0) {
            return "Nol Rupiah";
        }

        java.math.BigInteger intValue = number.toBigInteger();
        String numStr = intValue.toString();

        java.util.LinkedList<String> resultParts = new java.util.LinkedList<>();
        int scaleIndex = 0;

        while (numStr.length() > 0) {
            int endIndex = numStr.length();
            int startIndex = Math.max(0, endIndex - 3);
            int chunk = Integer.parseInt(numStr.substring(startIndex));
            numStr = numStr.substring(0, startIndex);

            if (chunk > 0) {
                String chunkWords;
                // Kasus khusus untuk "seribu"
                if (scaleIndex == 1 && chunk == 1) {
                    chunkWords = "seribu";
                } else {
                    chunkWords = convertThreeDigitsToWords(chunk) + (scaleIndex > 0 ? " " + SKALA[scaleIndex] : "");
                }
                resultParts.addFirst(chunkWords.trim());
            }
            scaleIndex++;
        }

        String result = String.join(" ", resultParts).replaceAll("\\s+", " ").trim();
        // Kapitalisasi huruf pertama dan tambahkan "Rupiah"
        return Character.toUpperCase(result.charAt(0)) + result.substring(1) + " Rupiah";
    }

    private static String convertThreeDigitsToWords(int n) {
        if (n == 0) return "";

        StringBuilder words = new StringBuilder();

        // Ratusan
        if (n >= 100) {
            if (n / 100 == 1) {
                words.append("seratus"); // Kasus khusus "seratus"
            } else {
                words.append(SATUAN[n / 100]).append(" ratus");
            }
            n %= 100;
            if (n > 0) words.append(" ");
        }

        // Puluhan & Belasan
        if (n >= 20) {
            String[] puluhan = {"", "", "dua puluh", "tiga puluh", "empat puluh", "lima puluh", "enam puluh", "tujuh puluh", "delapan puluh", "sembilan puluh"};
            words.append(puluhan[n / 10]);
            if (n % 10 > 0) {
                words.append(" ").append(SATUAN[n % 10]);
            }
        } else if (n >= 10) {
            String[] belasan = {"sepuluh", "sebelas", "dua belas", "tiga belas", "empat belas", "lima belas", "enam belas", "tujuh belas", "delapan belas", "sembilan belas"};
            words.append(belasan[n % 10]);
        } else if (n > 0) { // Satuan
            words.append(SATUAN[n]);
        }

        return words.toString();
    }

    private String generateFundRequestCode() {
        LocalDate now = LocalDate.now();
        int year = now.getYear() % 100; // ambil 2 digit
        int month = now.getMonthValue();
        String romanMonth = ROMAN_MONTHS[month - 1];

        // Hitung nomor urut di bulan ini
        Long countThisMonth = fundRequestRepository.countByMonthAndYear(month, now.getYear());
        Long sequence = countThisMonth + 1;

        return String.format("%s/%d/%s/%02d", PREFIX, sequence, romanMonth, year);
    }

}
