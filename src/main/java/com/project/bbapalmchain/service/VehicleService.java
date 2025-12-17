package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.VehicleDTO;
import com.project.bbapalmchain.dto.VehicleRequestDTO;
import com.project.bbapalmchain.mapper.VehicleMapper;
import com.project.bbapalmchain.model.Vehicle;
import com.project.bbapalmchain.repository.VehicleRepository;
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
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    private final VehicleMapper vehicleMapper;

    @Transactional(readOnly = true)
    public List<VehicleDTO> findAll() {
        final List<Vehicle> vehicleList = vehicleRepository.findByActiveTrue(Sort.by("id"));
        return vehicleList.stream()
                .map(vehicleMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleDTO get(Long id) {
        return vehicleRepository.findById(id)
                .map(vehicleMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(VehicleRequestDTO dto) {
        Vehicle entity = vehicleMapper.toEntity(dto);
        entity.setActive(true);

        return vehicleRepository.save(entity).getId();
    }

    public void update(Long id, VehicleRequestDTO dto) {
        Vehicle entity = vehicleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        Vehicle updatedEntity = vehicleMapper.toUpdate(entity, dto);
        vehicleRepository.save(updatedEntity);
    }

    public void delete(Long id) {
        Vehicle entity = vehicleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity.setActive(false);
        vehicleRepository.save(entity);
    }

}
