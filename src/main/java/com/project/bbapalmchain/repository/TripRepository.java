package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.dto.projection.*;
import com.project.bbapalmchain.model.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {


    @Query("""
    SELECT t FROM Trip t
    WHERE (:startDate IS NULL OR t.date >= CAST(:startDate AS date))
      AND (:endDate IS NULL OR t.date <= CAST(:endDate AS date))
      
      AND (
            (:millNull = TRUE AND t.mill IS NULL)
            OR
            ((:millNull IS NULL OR :millNull = FALSE) AND (:millIds IS NULL OR t.mill.id IN :millIds))
          )
          
      AND (
            (:driverNull = TRUE AND t.driver IS NULL)
            OR
            ((:driverNull IS NULL OR :driverNull = FALSE) AND (:driverIds IS NULL OR t.driver.id IN :driverIds))
          )
          
      AND (
            (:vehicleNull = TRUE AND t.vehicle IS NULL)
            OR
            ((:vehicleNull IS NULL OR :vehicleNull = FALSE) AND (:vehicleIds IS NULL OR t.vehicle.id IN :vehicleIds))
          )
          
      AND (
            (:contractorNull = TRUE AND t.contractor IS NULL)
            OR
            ((:contractorNull IS NULL OR :contractorNull = FALSE) AND (:contractorIds IS NULL OR t.contractor.id IN :contractorIds))
          )
          
      AND (
            (:afdelingNull = TRUE AND t.afdeling IS NULL)
            OR
            ((:afdelingNull IS NULL OR :afdelingNull = FALSE) AND (:afdelingIds IS NULL OR t.afdeling.id IN :afdelingIds))
          )
      AND (
             (:tripTypeNull = TRUE AND t.tripType IS NULL)
             OR
             ((:tripTypeNull IS NULL OR :tripTypeNull = FALSE) AND (:tripTypeIds IS NULL OR t.tripType.id IN :tripTypeIds))
           )
      
      AND (CAST(:loadWeightMin AS double) IS NULL OR t.loadWeightKg >= :loadWeightMin)
      AND (CAST(:loadWeightMax AS double) IS NULL OR t.loadWeightKg <= :loadWeightMax)
""")
    Page<Trip> findFilteredTrips(@Param("startDate") String startDate,
                                 @Param("endDate") String endDate,
                                 @Param("millIds") List<Long> millIds,
                                 @Param("driverIds") List<Long> driverIds,
                                 @Param("vehicleIds") List<Long> vehicleIds,
                                 @Param("contractorIds") List<Long> contractorIds,
                                 @Param("tripTypeIds") List<Long> tripTypes,
                                 @Param("afdelingIds") List<Long> afdelingIds,
                                 @Param("loadWeightMin") BigDecimal loadWeightMin,
                                 @Param("loadWeightMax") BigDecimal loadWeightMax,
                                 @Param("millNull") Boolean millNull,
                                 @Param("afdelingNull") Boolean afdelingNull,
                                 @Param("driverNull") Boolean driverNull,
                                 @Param("vehicleNull") Boolean vehicleNull,
                                 @Param("contractorNull") Boolean contractorNull,
                                 @Param("tripTypeNull") Boolean tripTypeNull,
                                 Pageable pageable);

    @Query("""
                SELECT t FROM Trip t
     WHERE (:startDate IS NULL OR t.date >= CAST(:startDate AS date))
       AND (:endDate IS NULL OR t.date <= CAST(:endDate AS date))
       AND (
             (:millNull = TRUE AND t.mill IS NULL)
             OR
             ((:millNull IS NULL OR :millNull = FALSE) AND (:millIds IS NULL OR t.mill.id IN :millIds))
           )
       AND (
             (:driverNull = TRUE AND t.driver IS NULL)
             OR
             ((:driverNull IS NULL OR :driverNull = FALSE) AND (:driverIds IS NULL OR t.driver.id IN :driverIds))
           )
       AND (
             (:vehicleNull = TRUE AND t.vehicle IS NULL)
             OR
             ((:vehicleNull IS NULL OR :vehicleNull = FALSE) AND (:vehicleIds IS NULL OR t.vehicle.id IN :vehicleIds))
           )
       AND (
             (:contractorNull = TRUE AND t.contractor IS NULL)
             OR
             ((:contractorNull IS NULL OR :contractorNull = FALSE) AND (:contractorIds IS NULL OR t.contractor.id IN :contractorIds))
           )
       AND (
             (:afdelingNull = TRUE AND t.afdeling IS NULL)
             OR
             ((:afdelingNull IS NULL OR :afdelingNull = FALSE) AND (:afdelingIds IS NULL OR t.afdeling.id IN :afdelingIds))
           )     
       AND (
             (:tripTypeNull = TRUE AND t.tripType IS NULL)
             OR
             ((:tripTypeNull IS NULL OR :tripTypeNull = FALSE) AND (:tripTypeIds IS NULL OR t.tripType.id IN :tripTypeIds))
           )
       AND (t.loadWeightKg >= :loadWeightMin)
       AND (t.loadWeightKg <= :loadWeightMax)
            ORDER BY t.date DESC
            """)
    List<Trip> findFilteredTripsForExport(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("millIds") List<Long> millIds,
            @Param("driverIds") List<Long> driverIds,
            @Param("vehicleIds") List<Long> vehicleIds,
            @Param("contractorIds") List<Long> contractorIds,
            @Param("tripTypeIds") List<Long> tripTypes,
            @Param("afdelingIds") List<Long> afdelingIds,
            @Param("loadWeightMin") BigDecimal loadWeightMin,
            @Param("loadWeightMax") BigDecimal loadWeightMax,
            @Param("millNull") Boolean millNull,
            @Param("afdelingNull") Boolean afdelingNull,
            @Param("driverNull") Boolean driverNull,
            @Param("vehicleNull") Boolean vehicleNull,
            @Param("contractorNull") Boolean contractorNull,
            @Param("tripTypeNull") Boolean tripTypeNull
    );


    @Query(value = """ 
                WITH current_period AS (
                    SELECT 
                        SUM(ptpn_rate * load_weight_kg) AS total_revenue,
                        SUM(contractor_rate * load_weight_kg + travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS total_expenses,
                        SUM(ptpn_rate * load_weight_kg - contractor_rate * load_weight_kg - 
                            (travel_allowance + loading_fee + consumption_fee) + additional_fee_1 + additional_fee_2 + additional_fee_3) AS total_profit_loss
                    FROM {h-schema}trip
                    WHERE (:startDate IS NULL OR date >= CAST(:startDate AS DATE))
                      AND (:endDate IS NULL OR date <= CAST(:endDate AS DATE))
                ),
                previous_period AS (
                    SELECT 
                        SUM(ptpn_rate * load_weight_kg) AS total_revenue,
                        SUM(contractor_rate * load_weight_kg + travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS total_expenses,
                        SUM(ptpn_rate * load_weight_kg - contractor_rate * load_weight_kg - 
                            (travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3)) AS total_profit_loss
                    FROM {h-schema}trip
                    WHERE (
                            :startDate IS NOT NULL AND :endDate IS NOT NULL AND
                            date BETWEEN 
                                (CAST(:startDate AS DATE) - make_interval(days := (CAST(:endDate AS DATE) - CAST(:startDate AS DATE)) + 1)) 
                                AND (CAST(:startDate AS DATE) - INTERVAL '1 day')
                        )
                )
                SELECT 
                    c.total_revenue as totalRevenue,
                    c.total_expenses as totalExpenses,
                    c.total_profit_loss as totalProfitLosses,
                    p.total_revenue as previousTotalRevenue,
                    p.total_expenses as previousTotalExpenses,
                    p.total_profit_loss as previousTotalProfitLosses,
                    CASE 
                        WHEN p.total_revenue = 0 OR p.total_revenue IS NULL THEN 0
                        ELSE ROUND(((c.total_revenue - p.total_revenue) / p.total_revenue) * 100, 2)
                    END AS revenueChangePercentage,
                    CASE 
                        WHEN p.total_expenses = 0 OR p.total_expenses IS NULL THEN 0
                        ELSE ROUND(((c.total_expenses - p.total_expenses) / p.total_expenses) * 100, 2)
                    END AS expenseChangePercentage,
                    CASE 
                        WHEN p.total_profit_loss = 0 OR p.total_profit_loss IS NULL THEN 0
                        ELSE ROUND(((c.total_profit_loss - p.total_profit_loss) / p.total_profit_loss) * 100, 2)
                    END AS profitLossChangePercentage
                FROM current_period c, previous_period p
            """, nativeQuery = true)
    DashboardSummaryProjection getSummary(@Param("startDate") String startDate, @Param("endDate") String endDate);


    @Query(value = """
                SELECT 
                    date,
                    SUM(
                        (ptpn_rate * load_weight_kg) 
                        - (contractor_rate * load_weight_kg) 
                        - (travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3)
                    ) AS total_profit_loss
                FROM {h-schema}trip
                WHERE 
                    (:startDate IS NULL OR date >= CAST(:startDate AS DATE))
                    AND (:endDate IS NULL OR date <= CAST(:endDate AS DATE))
                GROUP BY date
                ORDER BY date ASC
            """, nativeQuery = true)
    List<ProfitLossPerDayProjection> getProfitLossPerDay(@Param("startDate") String startDate, @Param("endDate") String endDate);


    @Query(value = """
            WITH params AS (
                SELECT :year AS year,
                       CASE 
                           WHEN (:year % 4 = 0 AND :year % 100 <> 0) OR (:year % 400 = 0)
                           THEN TRUE
                           ELSE FALSE
                       END AS is_leap
            ),
            calendar AS (
                SELECT 
                    m AS month,
                    d AS day
                FROM generate_series(1, 12) AS m,
                     generate_series(1, 31) AS d,
                     params
                WHERE (
                    (m = 2 AND ((params.is_leap AND d <= 29) OR (NOT params.is_leap AND d <= 28)))
                    OR (m IN (4, 6, 9, 11) AND d <= 30)
                    OR (m IN (1, 3, 5, 7, 8, 10, 12) AND d <= 31)
                )
            ),
            agg AS (
                SELECT
                    EXTRACT(MONTH FROM t.date) AS month,
                    EXTRACT(DAY FROM t.date) AS day,
                    ROUND(AVG(t.load_weight_kg), 2) AS avgLoadWeight
                FROM {h-schema}trip t
                WHERE EXTRACT(YEAR FROM t.date) = :year
                GROUP BY month, day
            )
            SELECT 
                c.month,
                c.day,
                COALESCE(a.avgLoadWeight, 0) AS avgLoadWeight
            FROM calendar c
            LEFT JOIN agg a ON c.month = a.month AND c.day = a.day
            ORDER BY c.month, c.day;
            """, nativeQuery = true)
    List<DashboardHeatMapByYearProjection> getHeatmapByYear(@Param("year") int year);

    @Query(value = """
            SELECT
                t.vehicle_id as vehicleId,
                v.license_plat_number as licensePlatNumber,
                v.vehicle_type as vehicleType,
                COUNT(t.id) AS totalTrips,
                SUM(t.load_weight_kg) AS totalLoad,
                SUM(t.ptpn_rate * t.load_weight_kg) AS totalRevenue,
                SUM(t.contractor_rate * t.load_weight_kg) AS totalContractorExpenses,
                SUM(t.travel_allowance + t.loading_fee + t.consumption_fee + t.additional_fee_1 + t.additional_fee_2 + t.additional_fee_3) AS totalFeeOperational,
                SUM(t.contractor_rate * t.load_weight_kg) + SUM(t.travel_allowance + t.loading_fee + t.consumption_fee + t.additional_fee_1 + t.additional_fee_2 + t.additional_fee_3) AS totalExpenses,
                SUM(t.ptpn_rate * t.load_weight_kg) - (SUM(t.contractor_rate * t.load_weight_kg) + SUM(t.travel_allowance + t.loading_fee + t.consumption_fee + t.additional_fee_1 + t.additional_fee_2 + t.additional_fee_3)) AS profitLoss
            FROM
                {h-schema}trip t
            LEFT JOIN 
                {h-schema}vehicle v ON t.vehicle_id = v.id
            WHERE
                (:startDate IS NULL OR t.date >= CAST(:startDate AS DATE))
                AND (:endDate IS NULL OR t.date <= CAST(:endDate AS DATE))
                AND (:isAll = TRUE OR t.vehicle_id IN (:vehicleIds))
            GROUP BY
                t.vehicle_id, v.license_plat_number, v.vehicle_type
            ORDER BY
                profitLoss DESC
            """, nativeQuery = true)
    List<SummaryPerVehicleProjection> getSummaryPerVehicle(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("vehicleIds") List<Long> vehicleIds, @Param("isAll") boolean isAll);

    @Query(value = """
                SELECT
                                     t.mill_id as millId,
                                     m.name as millName,
                                     COUNT(*) AS totalTrips,
                                     SUM(load_weight_kg) AS totalLoad,
                                     SUM(ptpn_rate * load_weight_kg) AS totalRevenue,
                                     SUM(contractor_rate * load_weight_kg) AS totalContractorExpenses,
                                     SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS totalFeeOperational,
            
                                     SUM(contractor_rate * load_weight_kg) +
                                     SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS totalExpenses,
            
                                     SUM(ptpn_rate * load_weight_kg) -
                                     (
                                         SUM(contractor_rate * load_weight_kg) +
                                         SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3)
                                     ) AS profitLoss
            
                                 FROM
                                     {h-schema}trip t
                                 LEFT JOIN {h-schema}mill m ON t.mill_id = m.id
                                 WHERE
                     (:startDate IS NULL OR date >= CAST(:startDate AS DATE))
                     AND (:endDate IS NULL OR date <= CAST(:endDate AS DATE))
                     AND (:isAll = TRUE OR mill_id IN (:millIds))
                                 GROUP BY t.mill_id, m.name
                                 ORDER BY profitLoss DESC
            """, nativeQuery = true)
    List<SummaryPerMillProjection> getSummaryPerMill(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("millIds") List<Long> millIds, @Param("isAll") boolean isAll);

    @Query(value = """
                SELECT
                                     t.afdeling_id as afdelingId,
                                     a.name as afdelingName,
                                     COUNT(*) AS totalTrips,
                                     SUM(load_weight_kg) AS totalLoad,
                                     SUM(ptpn_rate * load_weight_kg) AS totalRevenue,
                                     SUM(contractor_rate * load_weight_kg) AS totalContractorExpenses,
                                     SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS totalFeeOperational,
            
                                     SUM(contractor_rate * load_weight_kg) +
                                     SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS totalExpenses,
            
                                     SUM(ptpn_rate * load_weight_kg) -
                                     (
                                         SUM(contractor_rate * load_weight_kg) +
                                         SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3)
                                     ) AS profitLoss
            
                                 FROM
                                     {h-schema}trip t
                                 LEFT JOIN {h-schema}afdeling a ON t.afdeling_id = a.id
                                 WHERE
                     (:startDate IS NULL OR date >= CAST(:startDate AS DATE))
                     AND (:endDate IS NULL OR date <= CAST(:endDate AS DATE))
                     AND (:isAll = TRUE OR afdeling_id IN (:afdelingIds))
                                 GROUP BY
                                     t.afdeling_id, a.name
                                 ORDER BY
                                     profitLoss DESC
            """, nativeQuery = true)
    List<SummaryPerAfdelingProjection> getSummaryPerAfdeling(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("afdelingIds") List<Long> afdelingIds, @Param("isAll") boolean isAll);

    @Query(value = """
                SELECT
                    t.contractor_id as contractorId,
                    c.name as contractorName,
                    c.phone_number as contractorPhoneNumber,
                    COUNT(*) AS totalTrips,
                    SUM(load_weight_kg) AS totalLoad,
                    SUM(ptpn_rate * load_weight_kg) AS totalRevenue,
                    SUM(contractor_rate * load_weight_kg) AS totalContractorExpenses,
                    SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS totalFeeOperational,
                    SUM(contractor_rate * load_weight_kg) + SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS totalExpenses,
                    SUM(ptpn_rate * load_weight_kg) - (SUM(contractor_rate * load_weight_kg) + SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3)) AS profitLoss,
                    STRING_AGG(DISTINCT CAST(t.vehicle_id AS VARCHAR), ',') AS vehicleIds
                FROM
                    {h-schema}trip t
                LEFT JOIN {h-schema}contractor c ON t.contractor_id = c.id
                WHERE
                    (:startDate IS NULL OR t.date >= CAST(:startDate AS DATE))
                    AND (:endDate IS NULL OR t.date <= CAST(:endDate AS DATE))
                    AND (:isAll = TRUE OR t.contractor_id IN (:contractorIds))
                GROUP BY
                    t.contractor_id, c.name, c.phone_number
                ORDER BY
                    profitLoss DESC
            """, nativeQuery = true)
    List<SummaryPerContractorProjection> getSummaryPerContractor(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("contractorIds") List<Long> contractorIds, @Param("isAll") boolean isAll);

    @Query(value = """
                SELECT
                                     t.driver_id as driverId,
                                     d.name as driverName,
                                     d.license_number as driverLicenseNumber,
                                     COUNT(*) AS totalTrips,
                                     SUM(load_weight_kg) AS totalLoad,
                                     SUM(ptpn_rate * load_weight_kg) AS totalRevenue,
                                     SUM(contractor_rate * load_weight_kg) AS totalContractorExpenses,
                                     SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS totalFeeOperational,
            
                                     SUM(contractor_rate * load_weight_kg) +
                                     SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS totalExpenses,
            
                                     SUM(ptpn_rate * load_weight_kg) -
                                     (
                                         SUM(contractor_rate * load_weight_kg) +
                                         SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3)
                                     ) AS profitLoss
            
                                 FROM
                                     {h-schema}trip t
                                 LEFT JOIN {h-schema}driver d ON t.driver_id = d.id
                                 WHERE
                     (:startDate IS NULL OR date >= CAST(:startDate AS DATE))
                     AND (:endDate IS NULL OR date <= CAST(:endDate AS DATE))
                     AND (:isAll = TRUE OR driver_id IN (:driverIds))
                                 GROUP BY t.driver_id, d.name, d.license_number
                                 ORDER BY profitLoss DESC
            """, nativeQuery = true)
    List<SummaryPerDriverProjection> getSummaryPerDriver(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("driverIds") List<Long> driverIds, @Param("isAll") boolean isAll);

    @Query(value = """
            SELECT
                                 t.trip_type_id AS tripTypeId,
                                 tt.name AS tripTypeName,
                                 COUNT(*) AS totalTrips,
                                 SUM(load_weight_kg) AS totalLoad,
                                 SUM(ptpn_rate * load_weight_kg) AS totalRevenue,
                                 SUM(contractor_rate * load_weight_kg) AS totalContractorExpenses,
                                 SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS totalFeeOperational,
        
                                 SUM(contractor_rate * load_weight_kg) +
                                 SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3) AS totalExpenses,
        
                                 SUM(ptpn_rate * load_weight_kg) -
                                 (
                                     SUM(contractor_rate * load_weight_kg) +
                                     SUM(travel_allowance + loading_fee + consumption_fee + additional_fee_1 + additional_fee_2 + additional_fee_3)
                                 ) AS profitLoss
        
                             FROM
                                 {h-schema}trip t
                             LEFT JOIN {h-schema}trip_type tt ON t.trip_type_id = tt.id -- JOIN ke tabel TripType
                             WHERE
                 (:startDate IS NULL OR date >= CAST(:startDate AS DATE))
                 AND (:endDate IS NULL OR date <= CAST(:endDate AS DATE))
                 AND (:isAll = TRUE OR t.trip_type_id IN (:tripTypeIds))
                             GROUP BY
                                 t.trip_type_id, tt.name
                             ORDER BY
                                 profitLoss DESC
        """, nativeQuery = true)
    List<SummaryPerTripTypeProjection> getSummaryPerTripType(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("tripTypeIds") List<Long> tripTypeIds,
            @Param("isAll") boolean isAll
    );

}
