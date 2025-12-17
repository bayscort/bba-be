package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.TripTypeDTO;
import com.project.bbapalmchain.dto.TripTypeRequestDTO;
import com.project.bbapalmchain.mapper.TripTypeMapper;
import com.project.bbapalmchain.model.TripType;
import com.project.bbapalmchain.repository.TripTypeRepository;
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
public class TripTypeService {

    private final TripTypeRepository tripTypeRepository;

    private final TripTypeMapper tripTypeMapper;

    @Transactional(readOnly = true)
    public List<TripTypeDTO> findAll() {
        final List<TripType> tripTypeList = tripTypeRepository.findByActiveTrue(Sort.by("id"));
        return tripTypeList.stream()
                .map(tripTypeMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TripTypeDTO get(Long id) {
        return tripTypeRepository.findById(id)
                .map(tripTypeMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(TripTypeRequestDTO dto) {
        TripType entity = tripTypeMapper.toEntity(dto);
        entity.setActive(true);

        return tripTypeRepository.save(entity).getId();
    }

    public void update(Long id, TripTypeRequestDTO dto) {
        TripType entity = tripTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        TripType updatedEntity = tripTypeMapper.toUpdate(entity, dto);
        tripTypeRepository.save(updatedEntity);
    }

    public void delete(Long id) {
        TripType entity = tripTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity.setActive(false);
        tripTypeRepository.save(entity);
    }

}
