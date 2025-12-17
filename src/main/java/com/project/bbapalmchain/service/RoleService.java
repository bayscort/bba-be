package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.*;
import com.project.bbapalmchain.mapper.RoleMapper;
import com.project.bbapalmchain.model.*;
import com.project.bbapalmchain.repository.RoleMenuPermissionRepository;
import com.project.bbapalmchain.repository.RoleRepository;
import com.project.bbapalmchain.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;
    private final RoleMenuPermissionRepository roleMenuPermissionRepository;

    @Transactional(readOnly = true)
    public List<RoleResponseDTO> findAll() {
        final List<Role> roleList = roleRepository.findByActiveTrue(Sort.by("id"));
        return roleList.stream()
                .map(roleMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponseDTO get(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(RoleRequestDTO dto) {
        Role entity = roleMapper.toEntity(dto);
        entity.setActive(true);
        return roleRepository.save(entity).getId();
    }

    public void update(Long id, RoleRequestDTO dto) {
        Role entity = roleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        roleMapper.toUpdate(entity, dto);
        roleRepository.save(entity);
    }

    public void delete(Long id) {
        Role entity = roleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity.setActive(false);
        roleRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<RoleDetailResponseDTO> getMenuPermission() {
        List<Role> roles = roleRepository.findAll();
        List<RoleMenuPermission> allMappings = roleMenuPermissionRepository.findAll();

        Map<Long, RoleDetailResponseDTO> roleMap = new HashMap<>();

        for (Role role : roles) {
            RoleDetailResponseDTO dto = new RoleDetailResponseDTO();
            dto.setRole(new RoleResponseDTO(role.getId(), role.getName()));
            dto.setMenuList(new ArrayList<>());
            roleMap.put(role.getId(), dto);
        }

        Map<Long, Map<Long, MenuDetailDTO>> menuMapByRole = new HashMap<>();

        for (RoleMenuPermission rmp : allMappings) {
            Long roleId = rmp.getRole().getId();
            Long menuId = rmp.getMenu().getId();
            String menuName = rmp.getMenu().getName();

            RoleDetailResponseDTO roleDTO = roleMap.get(roleId);
            if (roleDTO == null) continue;

            menuMapByRole.putIfAbsent(roleId, new HashMap<>());
            Map<Long, MenuDetailDTO> menuMap = menuMapByRole.get(roleId);

            MenuDetailDTO menuDTO = menuMap.get(menuId);
            if (menuDTO == null) {
                menuDTO = new MenuDetailDTO();
                menuDTO.setId(menuId);
                menuDTO.setName(menuName);
                menuDTO.setPermissionList(new ArrayList<>());
                menuMap.put(menuId, menuDTO);
                roleDTO.getMenuList().add(menuDTO);
            }

            Permission perm = rmp.getPermission();
            PermissionResponseDTO permDTO = new PermissionResponseDTO();
            permDTO.setId(perm.getId());
            permDTO.setOperation(perm.getOperation());

            menuDTO.getPermissionList().add(permDTO);
        }

        return new ArrayList<>(roleMap.values());
    }

}
