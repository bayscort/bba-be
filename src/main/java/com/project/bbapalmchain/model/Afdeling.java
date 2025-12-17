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
public class Afdeling extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "estate_id")
    private Estate estate;

    @OneToMany(mappedBy = "afdeling", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Block> blockList = new ArrayList<>();

}
