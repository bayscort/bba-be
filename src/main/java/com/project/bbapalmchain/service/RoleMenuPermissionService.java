package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.*;
import com.project.bbapalmchain.mapper.RoleMenuPermissionMapper;
import com.project.bbapalmchain.model.Menu;
import com.project.bbapalmchain.model.Permission;
import com.project.bbapalmchain.model.Role;
import com.project.bbapalmchain.model.RoleMenuPermission;
import com.project.bbapalmchain.repository.MenuRepository;
import com.project.bbapalmchain.repository.PermissionRepository;
import com.project.bbapalmchain.repository.RoleRepository;
import com.project.bbapalmchain.repository.RoleMenuPermissionRepository;
import com.project.bbapalmchain.util.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleMenuPermissionService {

    private final RoleMenuPermissionRepository roleMenuPermissionRepository;
    private final RoleRepository roleRepository;

    private final RoleMenuPermissionMapper roleMenuPermissionMapper;
    private final MenuRepository menuRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<RoleMenuPermissionResponseDTO> findAll() {
        final List<RoleMenuPermission> roleMenuPermissionList = roleMenuPermissionRepository.findAll(Sort.by("id"));
        return roleMenuPermissionList.stream()
                .map(roleMenuPermissionMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleMenuPermissionResponseDTO get(Long id) {
        return roleMenuPermissionRepository.findById(id)
                .map(roleMenuPermissionMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(RoleMenuPermissionRequestDTO dto) {
        RoleMenuPermission entity = roleMenuPermissionMapper.toEntity(dto);

        Role role = roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
        entity.setRole(role);

        Menu menu = menuRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Menu not found"));
        entity.setMenu(menu);

        Permission permission = permissionRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Permission not found"));
        entity.setPermission(permission);

        return roleMenuPermissionRepository.save(entity).getId();
    }

    public void update(Long id, RoleMenuPermissionRequestDTO dto) {
        RoleMenuPermission entity = roleMenuPermissionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        RoleMenuPermission updatedEntity = roleMenuPermissionMapper.toUpdate(entity, dto);
        Role role = roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
        updatedEntity.setRole(role);

        Menu menu = menuRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Menu not found"));
        updatedEntity.setMenu(menu);

        Permission permission = permissionRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Permission not found"));
        updatedEntity.setPermission(permission);
        roleMenuPermissionRepository.save(entity);
    }

    public void delete(Long id) {
        roleMenuPermissionRepository.deleteById(id);
    }

    public void updateRoleMenuPermissions(Long roleId, RoleMenuPermissionUpdateRequestDTO request) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        roleMenuPermissionRepository.deleteByRoleId(roleId);

        List<RoleMenuPermission> newPermissions = new ArrayList<>();

        for (MenuPermissionUpdateRequestDTO menuPermission : request.getMenuPermissions()) {
            Menu menu = menuRepository.findById(menuPermission.getMenuId())
                    .orElseThrow(() -> new EntityNotFoundException("Menu not found with id: " + menuPermission.getMenuId()));

            for (Long permissionId : menuPermission.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + permissionId));

                RoleMenuPermission roleMenuPermission = new RoleMenuPermission();
                roleMenuPermission.setRole(role);
                roleMenuPermission.setMenu(menu);
                roleMenuPermission.setPermission(permission);
                newPermissions.add(roleMenuPermission);
            }
        }
        roleMenuPermissionRepository.saveAll(newPermissions);
    }

}
