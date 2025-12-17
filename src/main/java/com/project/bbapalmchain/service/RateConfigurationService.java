package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.RateConfigurationRequestDTO;
import com.project.bbapalmchain.dto.RateConfigurationResponseDTO;
import com.project.bbapalmchain.mapper.RateConfigurationMapper;
import com.project.bbapalmchain.model.RateConfiguration;
import com.project.bbapalmchain.model.RateVersioning;
import com.project.bbapalmchain.repository.AfdelingRepository;
import com.project.bbapalmchain.repository.MillRepository;
import com.project.bbapalmchain.repository.RateConfigurationRepository;
import com.project.bbapalmchain.repository.RateVersioningRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RateConfigurationService {

    private final RateConfigurationRepository rateConfigurationRepository;
    private final RateVersioningRepository rateVersioningRepository;
    private final AfdelingRepository afdelingRepository;
    private final MillRepository millRepository;

    private final RateConfigurationMapper rateConfigurationMapper;

    public RateConfigurationResponseDTO createRate(RateConfigurationRequestDTO dto) {

        List<RateVersioning> oldVersions = rateVersioningRepository
                .findActiveByAfdelingAndMill(dto.getAfdelingId(), dto.getMillId());

        for (RateVersioning v : oldVersions) {
            v.setActive(false);
        }
        rateVersioningRepository.saveAll(oldVersions);

        RateVersioning version = new RateVersioning();
        version.setEffectiveDate(LocalDate.now());
//        version.setUpdatedBy(req.updatedBy());
        version.setActive(true);
        rateVersioningRepository.save(version);

        RateConfiguration config = new RateConfiguration();
        config.setAfdeling(afdelingRepository.findById(dto.getAfdelingId()).orElse(null));
        config.setMill(millRepository.findById(dto.getMillId()).orElse(null));
        config.setPtpnRate(dto.getPtpnRate());
        config.setContractorRate(dto.getContractorRate());
        config.setLabel(generateLabel(config));
        config.setRateVersioning(version);

        RateConfiguration saved = rateConfigurationRepository.save(config);
        return rateConfigurationMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public RateConfigurationResponseDTO getActiveRate(Long afdelingId, Long millId) {
        return rateConfigurationRepository.findEffectiveRate(afdelingId, millId, LocalDate.now())
                .map(rateConfigurationMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Rate aktif not found"));
    }

    @Transactional(readOnly = true)
    public List<RateConfigurationResponseDTO> getHistory(Long afdelingId, Long millId) {
        return rateConfigurationRepository.findAllByAfdelingIdAndMillIdOrderByRateVersioningEffectiveDateDesc(afdelingId, millId)
                .stream()
                .map(rateConfigurationMapper::toDTO)
                .toList();
    }

    private String generateLabel(RateConfiguration c) {
        return c.getAfdeling().getName() + " - " + c.getMill().getName();
    }

    @Transactional(readOnly = true)
    public List<RateConfigurationResponseDTO> getAllActiveRates() {
        return rateConfigurationRepository.findAllActiveRates()
                .stream()
                .map(rateConfig -> {
                    RateConfigurationResponseDTO dto = rateConfigurationMapper.toDTO(rateConfig);

                    if (rateConfig.getAfdeling() != null &&
                            rateConfig.getAfdeling().getEstate() != null) {

                        String estateName = rateConfig.getAfdeling().getEstate().getName();

                        if (dto.getAfdeling() != null) {
                            dto.getAfdeling().setEstateName(estateName);
                        }
                    }

                    return dto;
                })
                .toList();
    }


}
