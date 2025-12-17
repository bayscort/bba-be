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
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FundRequest extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fundRequestCode;

    private Long nextApprovalRoleId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    private BigDecimal totalAmount;

    private String totalAmountInWords;

    @OneToMany(mappedBy = "fundRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FundRequestApprovalLog> fundRequestApprovalLogList = new ArrayList<>();

    @OneToMany(mappedBy = "fundRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FundRequestItem> fundRequestItemList = new ArrayList<>();

    public void addDetail(FundRequestItem fundRequestItem) {
        fundRequestItemList.add(fundRequestItem);
        fundRequestItem.setFundRequest(this);
    }

    public void removeDetail(FundRequestItem fundRequestItem) {
        fundRequestItemList.remove(fundRequestItem);
        fundRequestItem.setFundRequest(null);
    }

    public void clearDetails() {
        fundRequestItemList.forEach(fri -> fri.setFundRequest(null));
        fundRequestItemList.clear();
    }

    public void setDetails(List<FundRequestItem> details) {
        clearDetails();
        if (details != null) {
            details.forEach(this::addDetail);
        }
    }

}
