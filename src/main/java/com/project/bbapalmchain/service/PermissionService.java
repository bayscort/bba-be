package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.PermissionRequestDTO;
import com.project.bbapalmchain.dto.PermissionResponseDTO;
import com.project.bbapalmchain.mapper.PermissionMapper;
import com.project.bbapalmchain.model.Permission;
import com.project.bbapalmchain.repository.PermissionRepository;
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
public class PermissionService {

    private final PermissionRepository permissionRepository;

    private final PermissionMapper permissionMapper;

    @Transactional(readOnly = true)
    public List<PermissionResponseDTO> findAll() {
        final List<Permission> permissionList = permissionRepository.findAll(Sort.by("id"));
        return permissionList.stream()
                .map(permissionMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermissionResponseDTO get(Long id) {
        return permissionRepository.findById(id)
                .map(permissionMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(PermissionRequestDTO dto) {
        Permission entity = permissionMapper.toEntity(dto);
        return permissionRepository.save(entity).getId();
    }

    public void update(Long id, PermissionRequestDTO dto) {
        Permission entity = permissionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        permissionMapper.toUpdate(entity, dto);
        permissionRepository.save(entity);
    }

    public void delete(Long id) {
        permissionRepository.deleteById(id);
    }

}
