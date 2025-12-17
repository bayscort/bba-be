package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.DriverDTO;
import com.project.bbapalmchain.mapper.DriverMapper;
import com.project.bbapalmchain.model.Driver;
import com.project.bbapalmchain.repository.DriverRepository;
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
public class DriverService {

    private final DriverRepository driverRepository;

    private final DriverMapper driverMapper;

    @Transactional(readOnly = true)
    public List<DriverDTO> findAll() {
        final List<Driver> driverList = driverRepository.findByActiveTrue(Sort.by("id"));
        return driverList.stream()
                .map(driverMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DriverDTO get(Long id) {
        return driverRepository.findById(id)
                .map(driverMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(DriverDTO dto) {
        Driver entity = driverMapper.toEntity(dto);
        entity.setActive(true);
        return driverRepository.save(entity).getId();
    }

    public void update(Long id, DriverDTO dto) {
        Driver entity = driverRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        driverMapper.toUpdate(entity, dto);
        driverRepository.save(entity);
    }

    public void delete(Long id) {
        Driver entity = driverRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity.setActive(false);
        driverRepository.save(entity);
    }

}
