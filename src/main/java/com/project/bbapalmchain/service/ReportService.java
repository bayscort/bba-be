package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.*;
import com.project.bbapalmchain.dto.projection.CashflowProjection;
import com.project.bbapalmchain.dto.projection.SummaryFinancePerItemProjection;
import com.project.bbapalmchain.dto.projection.SummaryPerContractorProjection;
import com.project.bbapalmchain.enums.VehicleType;
import com.project.bbapalmchain.mapper.*;
import com.project.bbapalmchain.model.TripType;
import com.project.bbapalmchain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final ReceiptRepository receiptRepository;
    private final ExpenditureRepository expenditureRepository;

    private final TripMapper tripMapper;
    private final VehicleMapper vehicleMapper;

    @Transactional(readOnly = true)
    public List<SummaryPerAfdelingDTO> getSummaryPerAfdeling(String startDate, String endDate, List<Long> afdelingIds) {
        boolean isAll = (afdelingIds == null || afdelingIds.isEmpty());
        List<Long> safeAfdelingIds = isAll ? List.of(-1L) : afdelingIds;

        return tripRepository.getSummaryPerAfdeling(startDate, endDate, safeAfdelingIds, isAll)
                .stream()
                .map(projection -> {
                    SummaryPerAfdelingDTO dto = tripMapper.toSummaryPerAfdelingDTO(projection);
                    if (projection.getAfdelingId() != null) {
                        AfdelingDTO afdelingDTO = new AfdelingDTO();
                        afdelingDTO.setId(projection.getAfdelingId());
                        afdelingDTO.setName(projection.getAfdelingName());
                        dto.setAfdeling(afdelingDTO);

                    } else {
                        dto.setAfdeling(null);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SummaryPerContractorDTO> getSummaryPerContractor(String startDate, String endDate, List<Long> contractorIds) {
        boolean isAll = (contractorIds == null || contractorIds.isEmpty());
        List<Long> safeContractorIds = isAll ? List.of(-1L) : contractorIds;

        List<SummaryPerContractorProjection> projections = tripRepository.getSummaryPerContractor(startDate, endDate, safeContractorIds, isAll);

        Set<Long> allVehicleIds = projections.stream()
                .map(SummaryPerContractorProjection::getVehicleIds)
                .filter(Objects::nonNull)
                .flatMap(ids -> Arrays.stream(ids.split(",")))
                .map(Long::parseLong)
                .collect(Collectors.toSet());

        Map<Long, VehicleDTO> vehicleMap = new HashMap<>();
        if (!allVehicleIds.isEmpty()) {
            vehicleRepository.findAllById(allVehicleIds).forEach(vehicle -> {
                vehicleMap.put(vehicle.getId(), vehicleMapper.toDTO(vehicle));
            });
        }

        return projections.stream()
                .map(projection -> {
                    SummaryPerContractorDTO dto = tripMapper.toSummaryPerContractorDTO(projection);

                    if (projection.getContractorId() != null) {
                        ContractorDTO contractorDTO = new ContractorDTO();
                        contractorDTO.setId(projection.getContractorId());
                        contractorDTO.setName(projection.getContractorName());
                        contractorDTO.setPhoneNumber(projection.getContractorPhoneNumber());
                        dto.setContractor(contractorDTO);
                    } else {
                        dto.setContractor(null);
                    }

                    List<VehicleDTO> vehicleDTOs = new ArrayList<>();
                    String vehicleIdsStr = projection.getVehicleIds();
                    if (vehicleIdsStr != null && !vehicleIdsStr.isEmpty()) {
                        Arrays.stream(vehicleIdsStr.split(","))
                                .map(Long::parseLong)
                                .forEach(id -> {
                                    VehicleDTO vehicleDTO = vehicleMap.get(id);
                                    if (vehicleDTO != null) {
                                        vehicleDTOs.add(vehicleDTO);
                                    }
                                });
                    }
                    dto.setVehicles(vehicleDTOs);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SummaryPerDriverDTO> getSummaryPerDriver(String startDate, String endDate, List<Long> driverIds) {
        boolean isAll = (driverIds == null || driverIds.isEmpty());

        List<Long> safeDriverIds = isAll ? List.of(-1L) : driverIds;


        return tripRepository.getSummaryPerDriver(startDate, endDate, safeDriverIds, isAll)
                .stream()
                .map(projection -> {
                    SummaryPerDriverDTO dto = tripMapper.toSummaryPerDriverDTO(projection);

                    if (projection.getDriverId() != null) {
                        DriverDTO driverDTO = new DriverDTO();
                        driverDTO.setId(projection.getDriverId());
                        driverDTO.setName(projection.getDriverName());
                        driverDTO.setLicenseNumber(projection.getDriverLicenseNumber());
                        dto.setDriver(driverDTO);

                    } else {
                        dto.setDriver(null);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SummaryPerMillDTO> getSummaryPerMill(String startDate, String endDate, List<Long> millIds) {
        boolean isAll = (millIds == null || millIds.isEmpty());
        List<Long> safeMillIds = isAll ? List.of(-1L) : millIds;

        return tripRepository.getSummaryPerMill(startDate, endDate, safeMillIds, isAll)
                .stream()
                .map(projection -> {
                    SummaryPerMillDTO dto = tripMapper.toSummaryPerMillDTO(projection);

                    if (projection.getMillId() != null) {
                        MillResponseDTO millDTO = new MillResponseDTO();
                        millDTO.setId(projection.getMillId());
                        millDTO.setName(projection.getMillName());
                        dto.setMill(millDTO);

                    } else {
                        dto.setMill(null);
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SummaryPerVehicleDTO> getSummaryPerVehicle(String startDate, String endDate, List<Long> vehicleIds) {
        boolean isAll = (vehicleIds == null || vehicleIds.isEmpty());
        List<Long> safeVehicleIds = isAll ? List.of(-1L) : vehicleIds;

        return tripRepository.getSummaryPerVehicle(startDate, endDate, safeVehicleIds, isAll)
                .stream()
                .map(projection -> {
                    SummaryPerVehicleDTO dto = tripMapper.toSummaryPerVehicleDTO(projection);

                    if (projection.getVehicleId() != null) {
                        VehicleDTO vehicleDTO = new VehicleDTO();
                        vehicleDTO.setId(projection.getVehicleId());
                        vehicleDTO.setLicensePlatNumber(projection.getLicensePlatNumber());

                        String vehicleTypeStr = projection.getVehicleType();
                        if (vehicleTypeStr != null) {
                            try {
                                vehicleDTO.setVehicleType(VehicleType.valueOf(vehicleTypeStr));
                            } catch (IllegalArgumentException e) {
                                vehicleDTO.setVehicleType(null);
                            }
                        }
                        dto.setVehicle(vehicleDTO);
                    } else {
                        dto.setVehicle(null);
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }


    public List<SummaryFinancePerCategoryDTO> getSummaryFinance(
            String type,
            Long accountId,
            String startDateStr,
            String endDateStr
    ) {
        LocalDate endDate = (endDateStr == null || endDateStr.isEmpty())
                ? LocalDate.of(3000, 1, 1)
                : LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate startDate = (startDateStr == null || startDateStr.isEmpty())
                ? LocalDate.of(1900, 1, 1)
                : LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);

        List<SummaryFinancePerItemProjection> projections;

        if ("receipt".equalsIgnoreCase(type)) {
            projections = receiptRepository.findSummaryByFinanceItem(accountId, startDate, endDate);
        } else if ("expenditure".equalsIgnoreCase(type)) {
            projections = expenditureRepository.findSummaryByFinanceItem(accountId, startDate, endDate);
        } else {
            throw new IllegalArgumentException("Invalid report type specified. Must be 'receipt' or 'expenditure'.");
        }

        if (projections.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<SummaryFinancePerItemDTO>> grouped = projections.stream()
                .collect(Collectors.groupingBy(
                        SummaryFinancePerItemProjection::getItemCategory,
                        Collectors.mapping(
                                p -> new SummaryFinancePerItemDTO(
                                        p.getFinanceItem(),
                                        p.getTotalTransaction(),
                                        p.getTotalAmount()
                                ),
                                Collectors.toList()
                        )
                ));

        return grouped.entrySet().stream()
                .map(e -> {
                    List<SummaryFinancePerItemDTO> items = e.getValue();

                    Integer totalTransaction = items.stream()
                            .map(SummaryFinancePerItemDTO::getTotalTransaction)
                            .filter(Objects::nonNull)
                            .reduce(0, Integer::sum);

                    BigDecimal totalAmount = items.stream()
                            .map(SummaryFinancePerItemDTO::getTotalAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new SummaryFinancePerCategoryDTO(
                            e.getKey(),
                            items,
                            totalTransaction,
                            totalAmount
                    );
                })
                .sorted(Comparator.comparing(SummaryFinancePerCategoryDTO::getItemCategory))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SummaryPerTripTypeDTO> getSummaryPerTripType(String startDate, String endDate, List<Long> tripTypeIds) {
        boolean isAll = (tripTypeIds == null || tripTypeIds.isEmpty());
        List<Long> safeTripTypeIds = isAll ? List.of(-1L) : tripTypeIds;

        return tripRepository.getSummaryPerTripType(startDate, endDate, safeTripTypeIds, isAll)
                .stream()
                .map(projection -> {
                    SummaryPerTripTypeDTO dto = tripMapper.toSummaryPerTripTypeDTO(projection);

                    if (projection.getTripTypeId() != null) {
                        TripTypeDTO t = new TripTypeDTO();
                        t.setId(projection.getTripTypeId());
                        t.setName(projection.getTripTypeName());
                        dto.setTripType(t);

                    } else {
                        dto.setTripType(null);
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<CashflowDTO> getCashflow(String type, Long accountId, Integer year) {
        List<CashflowProjection> projections;

        // 1. Pilih Repository
        if ("receipt".equalsIgnoreCase(type)) {
            projections = receiptRepository.findCashflowByYear(accountId, year);
        } else if ("expenditure".equalsIgnoreCase(type)) {
            projections = expenditureRepository.findCashflowByYear(accountId, year);
        } else {
            throw new IllegalArgumentException("Invalid report type. Must be 'receipt' or 'expenditure'.");
        }

        if (projections.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Grouping Logic: Map<CategoryName, Map<ItemName, List<Projections>>>
        //    Projections di sini berisi pecahan per bulan
        Map<String, Map<String, List<CashflowProjection>>> groupedData = projections.stream()
                .collect(Collectors.groupingBy(
                        CashflowProjection::getItemCategory,
                        Collectors.groupingBy(CashflowProjection::getFinanceItem)
                ));

        // 3. Transformasi ke DTO
        return groupedData.entrySet().stream()
                .map(categoryEntry -> {
                    String categoryName = categoryEntry.getKey();
                    Map<String, List<CashflowProjection>> itemsMap = categoryEntry.getValue();

                    // a. Proses setiap Item dalam Kategori ini
                    List<CashflowItemDTO> itemDTOs = itemsMap.entrySet().stream()
                            .map(itemEntry -> {
                                String itemName = itemEntry.getKey();
                                List<CashflowProjection> monthlyData = itemEntry.getValue();

                                // Init array 12 bulan dengan 0
                                BigDecimal[] monthlyAmount = new BigDecimal[12];
                                Arrays.fill(monthlyAmount, BigDecimal.ZERO);
                                BigDecimal itemYearlyTotal = BigDecimal.ZERO;

                                // Isi array berdasarkan bulan yang ada di database
                                for (CashflowProjection p : monthlyData) {
                                    int monthIndex = p.getMonth() - 1; // DB 1-12 -> Array 0-11
                                    if (monthIndex >= 0 && monthIndex < 12) {
                                        monthlyAmount[monthIndex] = p.getTotalAmount();
                                        itemYearlyTotal = itemYearlyTotal.add(p.getTotalAmount());
                                    }
                                }

                                return new CashflowItemDTO(itemName, monthlyAmount, itemYearlyTotal);
                            })
                            .sorted(Comparator.comparing(CashflowItemDTO::getFinanceItem)) // Optional: sort by item name
                            .collect(Collectors.toList());

                    // b. Hitung Total Kategori per Bulan (Agregasi vertikal dari items)
                    BigDecimal[] categoryMonthlyTotal = new BigDecimal[12];
                    Arrays.fill(categoryMonthlyTotal, BigDecimal.ZERO);
                    BigDecimal categoryYearlyTotal = BigDecimal.ZERO;

                    for (CashflowItemDTO item : itemDTOs) {
                        categoryYearlyTotal = categoryYearlyTotal.add(item.getYearlyTotal());
                        for (int i = 0; i < 12; i++) {
                            categoryMonthlyTotal[i] = categoryMonthlyTotal[i].add(item.getMonthlyAmount()[i]);
                        }
                    }

                    return new CashflowDTO(categoryName, itemDTOs, categoryMonthlyTotal, categoryYearlyTotal);
                })
                .sorted(Comparator.comparing(CashflowDTO::getItemCategory)) // Sort kategori A-Z
                .collect(Collectors.toList());
    }

    public List<CashflowDTO> getCombineCashflow(String type, List<Long> accountIds, Integer year) {
        List<CashflowProjection> projections;

        if ("receipt".equalsIgnoreCase(type)) {
            projections = receiptRepository.findCashflowByAccountIdsAndYear(accountIds, year);
        } else if ("expenditure".equalsIgnoreCase(type)) {
            projections = expenditureRepository.findCashflowByAccountIdsAndYear(accountIds, year);
        } else {
            throw new IllegalArgumentException("Invalid report type. Must be 'receipt' or 'expenditure'.");
        }

        return mapProjectionsToCashflowDTO(projections);
    }

    private List<CashflowDTO> mapProjectionsToCashflowDTO(List<CashflowProjection> projections) {
        if (projections.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Grouping
        Map<String, Map<String, List<CashflowProjection>>> groupedData = projections.stream()
                .collect(Collectors.groupingBy(
                        CashflowProjection::getItemCategory,
                        Collectors.groupingBy(CashflowProjection::getFinanceItem)
                ));

        // 2. Mapping to DTO
        return groupedData.entrySet().stream()
                .map(categoryEntry -> {
                    String categoryName = categoryEntry.getKey();
                    Map<String, List<CashflowProjection>> itemsMap = categoryEntry.getValue();

                    List<CashflowItemDTO> itemDTOs = itemsMap.entrySet().stream()
                            .map(itemEntry -> {
                                String itemName = itemEntry.getKey();
                                List<CashflowProjection> monthlyData = itemEntry.getValue();

                                BigDecimal[] monthlyAmount = new BigDecimal[12];
                                Arrays.fill(monthlyAmount, BigDecimal.ZERO);
                                BigDecimal itemYearlyTotal = BigDecimal.ZERO;

                                for (CashflowProjection p : monthlyData) {
                                    int monthIndex = p.getMonth() - 1;
                                    if (monthIndex >= 0 && monthIndex < 12) {
                                        monthlyAmount[monthIndex] = p.getTotalAmount();
                                        itemYearlyTotal = itemYearlyTotal.add(p.getTotalAmount());
                                    }
                                }

                                return new CashflowItemDTO(itemName, monthlyAmount, itemYearlyTotal);
                            })
                            .sorted(Comparator.comparing(CashflowItemDTO::getFinanceItem))
                            .collect(Collectors.toList());

                    BigDecimal[] categoryMonthlyTotal = new BigDecimal[12];
                    Arrays.fill(categoryMonthlyTotal, BigDecimal.ZERO);
                    BigDecimal categoryYearlyTotal = BigDecimal.ZERO;

                    for (CashflowItemDTO item : itemDTOs) {
                        categoryYearlyTotal = categoryYearlyTotal.add(item.getYearlyTotal());
                        for (int i = 0; i < 12; i++) {
                            categoryMonthlyTotal[i] = categoryMonthlyTotal[i].add(item.getMonthlyAmount()[i]);
                        }
                    }

                    return new CashflowDTO(categoryName, itemDTOs, categoryMonthlyTotal, categoryYearlyTotal);
                })
                .sorted(Comparator.comparing(CashflowDTO::getItemCategory))
                .collect(Collectors.toList());
    }




}
