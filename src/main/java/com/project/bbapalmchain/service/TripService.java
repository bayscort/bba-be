package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.*;
import com.project.bbapalmchain.dto.projection.DashboardSummaryProjection;
import com.project.bbapalmchain.dto.projection.ProfitLossPerDayProjection;
import com.project.bbapalmchain.mapper.TripMapper;
import com.project.bbapalmchain.model.*;
import com.project.bbapalmchain.repository.*;
import com.project.bbapalmchain.util.NotFoundException;
import com.project.bbapalmchain.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TripService {

    private final TripRepository tripRepository;

    private final TripMapper tripMapper;
    private final MillRepository millRepository;
    private final AfdelingRepository afdelingRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final ContractorRepository contractorRepository;
    private final TripTypeRepository ttRepository;

    private final UserContext userContext;

    @Transactional(readOnly = true)
    public List<TripResponseDTO> getAll() {
        final List<Trip> tripList = tripRepository.findAll(Sort.by("id"));
        return tripList.stream()
                .map(tripMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<TripResponseDTO> findAll(Pageable pageable,
                                         String startDate,
                                         String endDate,
                                         List<Long> millIds,
                                         List<Long> afdelingIdsRequest,
                                         List<Long> driverIds,
                                         List<Long> vehicleIds,
                                         List<Long> contractorIds,
                                         List<Long> tripTypeIds,
                                         BigDecimal loadWeightMin,
                                         BigDecimal loadWeightMax,
                                         Boolean millNull,
                                         Boolean afdelingNull,
                                         Boolean driverNull,
                                         Boolean vehicleNull,
                                         Boolean contractorNull,
                                         Boolean tripTypeNull) {

        String startSafe = (startDate != null && !startDate.isBlank()) ? startDate : "1900-01-01";
        String endSafe = (endDate != null && !endDate.isBlank()) ? endDate : "3000-01-01";

        BigDecimal loadWeightMinSafe = (loadWeightMin == null) ? BigDecimal.valueOf(0) : loadWeightMin;
        BigDecimal loadWeightMaxSafe = (loadWeightMax == null) ? BigDecimal.valueOf(1000000) : loadWeightMax;

        List<Long> afdelingIdsToUse = afdelingIdsRequest;
        if (afdelingIdsToUse == null || afdelingIdsToUse.isEmpty()) {
            User user = userContext.getCurrentUser();
            Estate estate = user.getEstate();
            if (estate != null) {
                afdelingIdsToUse = afdelingRepository.findByEstateId(estate.getId())
                        .stream()
                        .map(Afdeling::getId)
                        .toList();
                if (afdelingIdsToUse.isEmpty()) {
                    afdelingIdsToUse = null;
                }
            }
        }

        Page<Trip> trips = tripRepository.findFilteredTrips(
                startSafe, endSafe,
                millIds, driverIds, vehicleIds, contractorIds, tripTypeIds,
                afdelingIdsToUse, loadWeightMinSafe, loadWeightMaxSafe,
                millNull, afdelingNull, driverNull, vehicleNull, contractorNull, tripTypeNull,
                pageable
        );

        return trips.map(tripMapper::toDTO);
    }


    @Transactional(readOnly = true)
    public byte[] exportTripsToExcel(String startDate,
                                     String endDate,
                                     List<Long> millIds,
                                     List<Long> afdelingIdsRequest,
                                     List<Long> driverIds,
                                     List<Long> vehicleIds,
                                     List<Long> contractorIds,
                                     List<Long> tripTypeIds,
                                     BigDecimal loadWeightMin,
                                     BigDecimal loadWeightMax,
                                     Boolean millNull,
                                     Boolean afdelingNull,
                                     Boolean driverNull,
                                     Boolean vehicleNull,
                                     Boolean contractorNull,
                                     Boolean tripTypeNull) {

        String startSafe = (startDate != null && !startDate.isBlank()) ? startDate : "1900-01-01";
        String endSafe = (endDate != null && !endDate.isBlank()) ? endDate : "3000-01-01";
        BigDecimal loadWeightMinSafe = (loadWeightMin == null) ? BigDecimal.valueOf(0) : loadWeightMin;
        BigDecimal loadWeightMaxSafe = (loadWeightMax == null) ? BigDecimal.valueOf(1000000) : loadWeightMax;

        List<Long> afdelingIdsToUse = afdelingIdsRequest;
        if (afdelingIdsToUse == null || afdelingIdsToUse.isEmpty()) {
            User user = userContext.getCurrentUser();
            Estate estate = user.getEstate();
            if (estate != null) {
                afdelingIdsToUse = afdelingRepository.findByEstateId(estate.getId())
                        .stream()
                        .map(Afdeling::getId)
                        .toList();
                if (afdelingIdsToUse.isEmpty()) {
                    afdelingIdsToUse = null; // Biarkan null agar query JPQL bekerja
                }
            }
        }

        // 2. Panggil repository baru untuk mendapatkan SEMUA data (List, bukan Page)
        List<Trip> trips = tripRepository.findFilteredTripsForExport(
                startSafe, endSafe,
                millIds, driverIds, vehicleIds, contractorIds, tripTypeIds,
                afdelingIdsToUse, loadWeightMinSafe, loadWeightMaxSafe, millNull, afdelingNull, driverNull, vehicleNull, contractorNull, tripTypeNull
        );

        // 3. Konversi List<Trip> menjadi List<TripResponseDTO>
        List<TripResponseDTO> tripDTOs = trips.stream().map(tripMapper::toDTO).toList();

        return createExcelFile(tripDTOs);
    }

    private byte[] createExcelFile(List<TripResponseDTO> tripDTOs) {
        // Menggunakan try-with-resources untuk memastikan workbook dan stream ditutup
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Data Perjalanan");

            // Header Style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Buat Header Row
            String[] headers = {"Tanggal", "Tipe", "Tujuan", "Asal", "Driver", "Kendaraan", "Kontraktor", "Berat Muatan (Kg)", "Rate (Rp)", "Rate Kontraktor (Rp)", "Pendapatan (Rp)", "Pelunasan Kontraktor (Rp)", "Uang Jalan (Rp)", "Uang Muat (Rp)", "Konsumsi (Rp)", "Additional Fee 1 (Rp)", "Additional Fee 2 (Rp)", "Additional Fee 3 (Rp)", "Total Pengeluaran (Rp)", "Laba/Rugi (Rp)"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Isi Data
            int rowNum = 1;
            for (TripResponseDTO trip : tripDTOs) {

                BigDecimal pendapatanPtpn = trip.getPtpnRate().multiply(trip.getLoadWeightKg());
                BigDecimal pelunasanKontraktor = trip.getContractorRate().multiply(trip.getLoadWeightKg());
                BigDecimal totalPengeluaran = pelunasanKontraktor.add(trip.getTravelAllowance().add(trip.getLoadingFee().add(trip.getConsumptionFee().add(trip.getAdditionalFee1().add(trip.getAdditionalFee2().add(trip.getAdditionalFee3()))))));
                BigDecimal labaRugi = pendapatanPtpn.subtract(totalPengeluaran);

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(trip.getDate().toString());
                row.createCell(1).setCellValue(trip.getTripType()==null ? "" : trip.getTripType().getName());
                row.createCell(2).setCellValue(trip.getMill()==null ? "" : trip.getMill().getName());
                row.createCell(3).setCellValue(trip.getAfdeling()==null ? "" : trip.getAfdeling().getName());
                row.createCell(4).setCellValue(trip.getDriver()==null ? "" : trip.getDriver().getName());
                row.createCell(5).setCellValue(trip.getVehicle()==null ? "" : trip.getVehicle().getLicensePlatNumber());
                row.createCell(6).setCellValue(trip.getContractor()==null ? "" : trip.getContractor().getName());
                row.createCell(7).setCellValue(trip.getLoadWeightKg().doubleValue());
                row.createCell(8).setCellValue(trip.getPtpnRate().doubleValue());
                row.createCell(9).setCellValue(trip.getContractorRate().doubleValue());
                row.createCell(10).setCellValue(pendapatanPtpn.doubleValue());
                row.createCell(11).setCellValue(pelunasanKontraktor.doubleValue());
                row.createCell(12).setCellValue(trip.getTravelAllowance().doubleValue());
                row.createCell(13).setCellValue(trip.getLoadingFee().doubleValue());
                row.createCell(14).setCellValue(trip.getConsumptionFee().doubleValue());
                row.createCell(15).setCellValue(trip.getAdditionalFee1().doubleValue());
                row.createCell(16).setCellValue(trip.getAdditionalFee2().doubleValue());
                row.createCell(17).setCellValue(trip.getAdditionalFee3().doubleValue());
                row.createCell(18).setCellValue(totalPengeluaran.doubleValue());
                row.createCell(19).setCellValue(labaRugi.doubleValue());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Gagal membuat file Excel: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public TripResponseDTO get(Long id) {
        return tripRepository.findById(id)
                .map(tripMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(TripRequestDTO dto) {
        Trip entity = tripMapper.toEntity(dto);

        Mill mill = millRepository.findById(dto.getMillId()).orElseThrow(() -> new RuntimeException("Mill not found"));
        entity.setMill(mill);

        Afdeling afdeling = afdelingRepository.findById(dto.getAfdelingId()).orElseThrow(() -> new RuntimeException("Afdeling not found"));
        entity.setAfdeling(afdeling);

        Driver driver = driverRepository.findById(dto.getDriverId()).orElseThrow(() -> new RuntimeException("Driver not found"));
        entity.setDriver(driver);

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId()).orElseThrow(() -> new RuntimeException("Vehicle not found"));
        entity.setVehicle(vehicle);

        Contractor contractor = contractorRepository.findById(dto.getContractorId()).orElseThrow(() -> new RuntimeException("Contractor not found"));
        entity.setContractor(contractor);

        TripType tt = ttRepository.findById(dto.getTripTypeId()).orElseThrow(() -> new RuntimeException("Trip Type not found"));
        entity.setTripType(tt);
        return tripRepository.save(entity).getId();
    }

    public void update(Long id, TripRequestDTO dto) {
        Trip entity = tripRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        Trip updatedEntity = tripMapper.toUpdate(entity, dto);

        Mill mill = millRepository.findById(dto.getMillId()).orElseThrow(() -> new RuntimeException("Mill not found"));
        updatedEntity.setMill(mill);

        Afdeling afdeling = afdelingRepository.findById(dto.getAfdelingId()).orElseThrow(() -> new RuntimeException("Afdeling not found"));
        updatedEntity.setAfdeling(afdeling);

        Driver driver = driverRepository.findById(dto.getDriverId()).orElseThrow(() -> new RuntimeException("Driver not found"));
        updatedEntity.setDriver(driver);

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId()).orElseThrow(() -> new RuntimeException("Vehicle not found"));
        updatedEntity.setVehicle(vehicle);

        Contractor contractor = contractorRepository.findById(dto.getContractorId()).orElseThrow(() -> new RuntimeException("Contractor not found"));
        updatedEntity.setContractor(contractor);

        TripType tt = ttRepository.findById(dto.getTripTypeId()).orElseThrow(() -> new RuntimeException("Trip Type not found"));
        updatedEntity.setTripType(tt);

        tripRepository.save(entity);
    }

    public void delete(Long id) {
        tripRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DashboardSummaryDTO getSummary(String startDate, String endDate) {
        DashboardSummaryProjection summaryProjection = tripRepository.getSummary(startDate, endDate);
        return tripMapper.toSummaryDTO(summaryProjection);
    }

    @Transactional(readOnly = true)
    public List<DashboardProfilLossPerDayDTO> getProfitLossPerDay(String startDate, String endDate) {
        List<ProfitLossPerDayProjection> profitLossPerDayProjectionList = tripRepository.getProfitLossPerDay(startDate, endDate);
        return profitLossPerDayProjectionList.stream()
                .map(tripMapper::toProfitLossPerDayDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DashboardHeatMapByYearDTO> getHeatmap(int year) {
        return tripRepository.getHeatmapByYear(year)
                .stream()
                .map(tripMapper::toHeatMapPerYearDTO
                )
                .collect(Collectors.toList());
    }

}
