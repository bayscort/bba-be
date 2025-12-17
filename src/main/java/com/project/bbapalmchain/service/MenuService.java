package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.MenuRequestDTO;
import com.project.bbapalmchain.dto.MenuResponseDTO;
import com.project.bbapalmchain.mapper.MenuMapper;
import com.project.bbapalmchain.model.Menu;
import com.project.bbapalmchain.repository.MenuRepository;
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
public class MenuService {

    private final MenuRepository menuRepository;

    private final MenuMapper menuMapper;

    @Transactional(readOnly = true)
    public List<MenuResponseDTO> findAll() {
        final List<Menu> menuList = menuRepository.findAll(Sort.by("id"));
        return menuList.stream()
                .map(menuMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuResponseDTO get(Long id) {
        return menuRepository.findById(id)
                .map(menuMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(MenuRequestDTO dto) {
        Menu entity = menuMapper.toEntity(dto);
        return menuRepository.save(entity).getId();
    }

    public void update(Long id, MenuRequestDTO dto) {
        Menu entity = menuRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        menuMapper.toUpdate(entity, dto);
        menuRepository.save(entity);
    }

    public void delete(Long id) {
        menuRepository.deleteById(id);
    }

}
