package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.FinanceItemDTO;
import com.project.bbapalmchain.mapper.FinanceItemMapper;
import com.project.bbapalmchain.model.FinanceItem;
import com.project.bbapalmchain.repository.FinanceItemRepository;
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
public class FinanceItemService {

    private final FinanceItemRepository financeItemRepository;

    private final FinanceItemMapper financeItemMapper;

    @Transactional(readOnly = true)
    public List<FinanceItemDTO> findAll() {
        final List<FinanceItem> financeItemList = financeItemRepository.findByActiveTrue(Sort.by("id"));
        return financeItemList.stream()
                .map(financeItemMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public FinanceItemDTO get(Long id) {
        return financeItemRepository.findById(id)
                .map(financeItemMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(FinanceItemDTO dto) {
        FinanceItem entity = financeItemMapper.toEntity(dto);
        entity.setActive(true);
        return financeItemRepository.save(entity).getId();
    }

    public void update(Long id, FinanceItemDTO dto) {
        FinanceItem entity = financeItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        financeItemMapper.toUpdate(entity, dto);
        financeItemRepository.save(entity);
    }

    public void delete(Long id) {
        FinanceItem entity = financeItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity.setActive(false);
        financeItemRepository.save(entity);
    }

}
