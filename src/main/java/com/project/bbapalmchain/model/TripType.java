package com.project.bbapalmchain.model;

import com.project.bbapalmchain.dto.base.AuditableEntity;
import com.project.bbapalmchain.enums.VehicleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Getter
@Setter
public class TripType extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Boolean active;

}
