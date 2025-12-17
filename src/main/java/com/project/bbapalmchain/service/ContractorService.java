package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.ContractorDTO;
import com.project.bbapalmchain.dto.ContractorRequestDTO;
import com.project.bbapalmchain.mapper.ContractorMapper;
import com.project.bbapalmchain.model.Contractor;
import com.project.bbapalmchain.repository.ContractorRepository;
import com.project.bbapalmchain.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContractorService {

    private final ContractorRepository contractorRepository;

    private final ContractorMapper contractorMapper;

    @Transactional(readOnly = true)
    public List<ContractorDTO> findAll() {
        final List<Contractor> contractorList = contractorRepository.findByActiveTrue(Sort.by("id"));
        return contractorList.stream()
                .map(contractorMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContractorDTO get(Long id) {
        return contractorRepository.findById(id)
                .map(contractorMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(ContractorRequestDTO dto) {
        Contractor entity = contractorMapper.toEntity(dto);
        entity.setActive(true);
        return contractorRepository.save(entity).getId();
    }

    public void update(Long id, ContractorRequestDTO dto) {
        Contractor entity = contractorRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        contractorMapper.toUpdate(entity, dto);
        contractorRepository.save(entity);
    }

    public void delete(Long id) {
        Contractor entity = contractorRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity.setActive(false);
        contractorRepository.save(entity);
    }

}
