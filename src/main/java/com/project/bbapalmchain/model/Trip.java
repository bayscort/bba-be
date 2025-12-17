package com.project.bbapalmchain.model;


import com.project.bbapalmchain.dto.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Trip extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "trip_type_id")
    private TripType tripType;

    @ManyToOne
    @JoinColumn(name = "mill_id")
    private Mill mill;

    @ManyToOne
    @JoinColumn(name = "afdeling_id")
    private Afdeling afdeling;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "contractor_id")
    private Contractor contractor;

    @Column(name = "load_weight_kg", nullable = false)
    private BigDecimal loadWeightKg;

    @Column(name = "ptpn_rate", nullable = false)
    private BigDecimal ptpnRate;

    @Column(name = "contractor_rate", nullable = false)
    private BigDecimal contractorRate;

    @Column(name = "travel_allowance", nullable = false)
    private BigDecimal travelAllowance;

    @Column(name = "loading_fee", nullable = false)
    private BigDecimal loadingFee;

    @Column(name = "consumption_fee", nullable = false)
    private BigDecimal consumptionFee;

    @Column(name = "additional_fee_1", nullable = false)
    private BigDecimal additionalFee1;

    @Column(name = "additional_fee_2", nullable = false)
    private BigDecimal additionalFee2;

    @Column(name = "additional_fee_3", nullable = false)
    private BigDecimal additionalFee3;

}
