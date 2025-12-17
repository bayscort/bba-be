package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.MillRequestDTO;
import com.project.bbapalmchain.dto.MillResponseDTO;
import com.project.bbapalmchain.mapper.MillMapper;
import com.project.bbapalmchain.model.Mill;
import com.project.bbapalmchain.repository.MillRepository;
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
public class MillService {

    private final MillRepository millRepository;

    private final MillMapper millMapper;

    @Transactional(readOnly = true)
    public List<MillResponseDTO> findAll() {
        final List<Mill> millList = millRepository.findByActiveTrue(Sort.by("id"));
        return millList.stream()
                .map(millMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MillResponseDTO get(Long id) {
        return millRepository.findById(id)
                .map(millMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(MillRequestDTO dto) {
        Mill entity = millMapper.toEntity(dto);
        entity.setActive(true);
        return millRepository.save(entity).getId();
    }

    public void update(Long id, MillRequestDTO dto) {
        Mill entity = millRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        millMapper.toUpdate(entity, dto);
        millRepository.save(entity);
    }

    public void delete(Long id) {
        Mill entity = millRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity.setActive(false);
        millRepository.save(entity);
    }

}
