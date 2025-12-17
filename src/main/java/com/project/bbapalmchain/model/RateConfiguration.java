package com.project.bbapalmchain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class RateConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "afdeling_id")
    private Afdeling afdeling;

    @ManyToOne
    @JoinColumn(name = "mill_id")
    private Mill mill;

    private BigDecimal ptpnRate;

    private BigDecimal contractorRate;

    private String label;

    @ManyToOne
    @JoinColumn(name = "rate_versioning_id")
    private RateVersioning rateVersioning;

}
