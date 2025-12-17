package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.AfdelingDTO;
import com.project.bbapalmchain.dto.BlockDTO;
import com.project.bbapalmchain.dto.EstateRequestDTO;
import com.project.bbapalmchain.dto.EstateResponseDTO;
import com.project.bbapalmchain.mapper.AfdelingMapper;
import com.project.bbapalmchain.mapper.BlockMapper;
import com.project.bbapalmchain.mapper.EstateMapper;
import com.project.bbapalmchain.model.Afdeling;
import com.project.bbapalmchain.model.Block;
import com.project.bbapalmchain.model.Estate;
import com.project.bbapalmchain.repository.EstateRepository;
import com.project.bbapalmchain.util.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EstateService {

    private final EstateRepository estateRepository;

    private final EstateMapper estateMapper;
    private final AfdelingMapper afdelingMapper;
    private final BlockMapper blockMapper;

    @Transactional(readOnly = true)
    public List<EstateResponseDTO> findAll() {
        final List<Estate> estateList = estateRepository.findByActiveTrue(Sort.by("id"));
        return estateList.stream()
                .map(estateMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstateResponseDTO get(Long id) {
        return estateRepository.findById(id)
                .map(estateMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

//    public Long create(EstateRequestDTO dto) {
//        Estate entity = estateMapper.toEntity(dto);
//        return estateRepository.save(entity).getId();
//    }

    public Long create(EstateRequestDTO dto) {
        Estate estate = new Estate();
        estate.setName(dto.getName());
        estate.setActive(true);

        List<Afdeling> afdelings = new ArrayList<>();
        if (dto.getAfdelingList() != null) {
            for (AfdelingDTO afdDto : dto.getAfdelingList()) {
                Afdeling afd = new Afdeling();
                afd.setName(afdDto.getName());
                afd.setEstate(estate);

                List<Block> blocks = new ArrayList<>();
                if (afdDto.getBlockList() != null) {
                    for (BlockDTO blkDto : afdDto.getBlockList()) {
                        Block blk = new Block();
                        blk.setName(blkDto.getName());
                        blk.setAfdeling(afd);
                        blocks.add(blk);
                    }
                }

                afd.setBlockList(blocks);
                afdelings.add(afd);
            }
        }

        estate.setAfdelingList(afdelings);
        return estateRepository.save(estate).getId();
    }

//    public void update(Long id, EstateRequestDTO dto) {
//        Estate entity = estateRepository.findById(id)
//                .orElseThrow(NotFoundException::new);
//        estateMapper.toUpdate(entity, dto);
//        estateRepository.save(entity);
//    }

    @Transactional
    public Estate update(Long id, EstateRequestDTO dto) {
        Estate estate = estateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estate not found with id: " + id));

        estateMapper.toUpdate(estate, dto); // update field utama estate seperti name

        // Siapkan map Afdeling yang sudah ada
        Map<Long, Afdeling> existingAfdelings = estate.getAfdelingList().stream()
                .filter(afd -> afd.getId() != null)
                .collect(Collectors.toMap(Afdeling::getId, Function.identity()));

        // Bersihkan dulu isi dari Afdeling lama
        estate.getAfdelingList().clear();

        for (AfdelingDTO afdDto : dto.getAfdelingList()) {
            Afdeling afd;

            if (afdDto.getId() != null && existingAfdelings.containsKey(afdDto.getId())) {
                afd = existingAfdelings.get(afdDto.getId());
                afdelingMapper.toUpdate(afd, afdDto);
            } else {
                afd = afdelingMapper.toEntity(afdDto);
            }

            afd.setEstate(estate); // hubungan balik

            // Proses Block
            Map<Long, Block> existingBlocks = afd.getBlockList() == null ? new HashMap<>() :
                    afd.getBlockList().stream()
                            .filter(b -> b.getId() != null)
                            .collect(Collectors.toMap(Block::getId, Function.identity()));

            if (afd.getBlockList() == null) {
                afd.setBlockList(new ArrayList<>());
            } else {
                afd.getBlockList().clear();
            }

            for (BlockDTO blkDto : afdDto.getBlockList()) {
                Block blk;

                if (blkDto.getId() != null && existingBlocks.containsKey(blkDto.getId())) {
                    blk = existingBlocks.get(blkDto.getId());
                    blockMapper.toUpdate(blk, blkDto);
                } else {
                    blk = blockMapper.toEntity(blkDto);
                }

                blk.setAfdeling(afd);
                afd.getBlockList().add(blk);
            }

            estate.getAfdelingList().add(afd);
        }

        return estateRepository.save(estate);
    }


    public void delete(Long id) {
        Estate entity = estateRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity.setActive(false);
        estateRepository.save(entity);
    }

}
