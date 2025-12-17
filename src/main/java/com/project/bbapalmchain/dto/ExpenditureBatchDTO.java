package com.project.bbapalmchain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ExpenditureBatchDTO {

    private LocalDate expenditureDate;
    private List<List<ExpenditureDTO>> batches;

}
