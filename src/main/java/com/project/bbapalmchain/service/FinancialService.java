package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.DashboardProfilLossPerDayDTO;
import com.project.bbapalmchain.dto.FinanceDashboardSummaryDTO;
import com.project.bbapalmchain.dto.projection.ProfitLossPerDayProjection;
import com.project.bbapalmchain.repository.ExpenditureRepository;
import com.project.bbapalmchain.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FinancialService {

    private final ReceiptRepository receiptRepository;
    private final ExpenditureRepository expenditureRepository;

    public FinanceDashboardSummaryDTO getSummary(Long accountId, String startDateStr, String endDateStr) {
        // 1. Tentukan rentang tanggal periode saat ini
        LocalDate endDate = (endDateStr == null || endDateStr.isEmpty())
                ? LocalDate.of(3000, 1, 1)
                : LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate startDate = (startDateStr == null || startDateStr.isEmpty())
                ? LocalDate.of(1900, 1, 1)
                : LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);

        // 2. Tentukan rentang tanggal periode sebelumnya
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate previousEndDate = startDate.minusDays(1);
        LocalDate previousStartDate = previousEndDate.minusDays(daysBetween - 1);

        // 3. Ambil data untuk periode saat ini dari repository
        BigDecimal totalRevenue = receiptRepository.sumTotalReceiptsByDateRange(accountId, startDate, endDate);
        BigDecimal totalExpenses = expenditureRepository.sumTotalExpendituresByDateRange(accountId, startDate, endDate);
        BigDecimal totalProfitLosses = totalRevenue.subtract(totalExpenses);

        // 4. Ambil data untuk periode sebelumnya dari repository
        BigDecimal previousTotalRevenue = receiptRepository.sumTotalReceiptsByDateRange(accountId, previousStartDate, previousEndDate);
        BigDecimal previousTotalExpenses = expenditureRepository.sumTotalExpendituresByDateRange(accountId, previousStartDate, previousEndDate);
        BigDecimal previousTotalProfitLosses = previousTotalRevenue.subtract(previousTotalExpenses);

        // 5. Hitung persentase perubahan
        Double revenueChangePercentage = calculatePercentageChange(totalRevenue, previousTotalRevenue);
        Double expenseChangePercentage = calculatePercentageChange(totalExpenses, previousTotalExpenses);
        Double profitLossChangePercentage = calculatePercentageChange(totalProfitLosses, previousTotalProfitLosses);

        // 6. Susun DTO untuk respons
        FinanceDashboardSummaryDTO summaryDTO = new FinanceDashboardSummaryDTO();
        summaryDTO.setAccountId(accountId);
        summaryDTO.setStartDate(startDate);
        summaryDTO.setEndDate(endDate);

        // Data Periode Saat Ini
        summaryDTO.setTotalRevenue(totalRevenue);
        summaryDTO.setTotalExpenses(totalExpenses);
        summaryDTO.setTotalProfitLosses(totalProfitLosses);

        // Data Periode Sebelumnya
        summaryDTO.setPreviousTotalRevenue(previousTotalRevenue);
        summaryDTO.setPreviousTotalExpenses(previousTotalExpenses);
        summaryDTO.setPreviousTotalProfitLosses(previousTotalProfitLosses);

        // Data Persentase Perubahan
        summaryDTO.setRevenueChangePercentage(revenueChangePercentage);
        summaryDTO.setExpenseChangePercentage(expenseChangePercentage);
        summaryDTO.setProfitLossChangePercentage(profitLossChangePercentage);

        return summaryDTO;
    }

    public List<DashboardProfilLossPerDayDTO> getProfitLossPerDay(Long accountId, String startDateStr, String endDateStr) {
        // 1. Tentukan rentang tanggal
        LocalDate endDate = (endDateStr == null || endDateStr.isEmpty())
                ? LocalDate.of(3000, 1, 1)
                : LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate startDate = (startDateStr == null || startDateStr.isEmpty())
                ? LocalDate.of(1900, 1, 1)
                : LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);

        // 2. Ambil data harian dari masing-masing repository
        List<ProfitLossPerDayProjection> dailyRevenues = receiptRepository.findDailyTotals(accountId, startDate, endDate);
        List<ProfitLossPerDayProjection> dailyExpenses = expenditureRepository.findDailyTotals(accountId, startDate, endDate);

        // 3. Gabungkan hasil menggunakan Map untuk menghitung profit/rugi per hari
        // Key: Tanggal, Value: Total Profit/Rugi
        Map<LocalDate, BigDecimal> profitLossMap = Stream.concat(dailyRevenues.stream(), dailyExpenses.stream())
                .collect(Collectors.toMap(
                        ProfitLossPerDayProjection::getDate,      // Kunci adalah tanggal
                        proj -> dailyRevenues.contains(proj) ? proj.getTotalProfitLoss() : proj.getTotalProfitLoss().negate(), // Jadikan expense negatif
                        BigDecimal::add                     // Jika tanggal sama, tambahkan nilainya
                ));

        // 4. Ubah Map menjadi List DTO dan urutkan berdasarkan tanggal
        return profitLossMap.entrySet().stream()
                .map(entry -> {
                    DashboardProfilLossPerDayDTO dto = new DashboardProfilLossPerDayDTO();
                    dto.setDate(entry.getKey());
                    dto.setTotalProfitLoss(entry.getValue());
                    return dto;
                })
                .sorted(Comparator.comparing(DashboardProfilLossPerDayDTO::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Helper method untuk menghitung persentase perubahan dari nilai lama ke baru.
     * Menangani kasus pembagian dengan nol.
     */
    private Double calculatePercentageChange(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            // Jika nilai sebelumnya 0, perubahan tidak dapat dihitung (atau dianggap 100% jika nilai baru > 0)
            return (current.compareTo(BigDecimal.ZERO) == 0) ? 0.0 : 100.0;
        }

        // Rumus: ((Baru - Lama) / Lama) * 100
        BigDecimal change = current.subtract(previous);
        BigDecimal percentage = change.divide(previous, 4, RoundingMode.HALF_UP); // 4 angka di belakang koma

        return percentage.multiply(BigDecimal.valueOf(100)).doubleValue();
    }

}
