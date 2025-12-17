package com.project.bbapalmchain.model;


import com.project.bbapalmchain.dto.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
public class Estate extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "estate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Afdeling> afdelingList = new ArrayList<>();

    private Boolean active;

    public void addDetail(Afdeling afdeling) {
        afdelingList.add(afdeling);
        afdeling.setEstate(this);
    }

    public void removeDetail(Afdeling afdeling) {
        afdelingList.remove(afdeling);
        afdeling.setEstate(null);
    }

    public void clearDetails() {
        afdelingList.forEach(afdeling -> afdeling.setEstate(null));
        afdelingList.clear();
    }

    public void setDetails(List<Afdeling> details) {
        clearDetails();
        if (details != null) {
            details.forEach(this::addDetail);
        }
    }

}
