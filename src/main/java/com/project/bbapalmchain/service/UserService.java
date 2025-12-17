package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.UserRequestDTO;
import com.project.bbapalmchain.dto.UserResponseDTO;
import com.project.bbapalmchain.mapper.UserMapper;
import com.project.bbapalmchain.model.Estate;
import com.project.bbapalmchain.model.Mill;
import com.project.bbapalmchain.model.Role;
import com.project.bbapalmchain.model.User;
import com.project.bbapalmchain.repository.EstateRepository;
import com.project.bbapalmchain.repository.RoleRepository;
import com.project.bbapalmchain.repository.UserRepository;
import com.project.bbapalmchain.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EstateRepository estateRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        final List<User> userList = userRepository.findByActiveTrue(Sort.by("id"));
        return userList.stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO get(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(UserRequestDTO dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username sudah digunakan.");
        }

        User entity = userMapper.toEntity(dto);
        entity.setActive(true);

        Role role = roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
        entity.setRole(role);

        if (null!=dto.getEstateId()) {
            Estate estate = estateRepository.findById(dto.getEstateId()).orElseThrow(() -> new RuntimeException("Estate not found"));
            entity.setEstate(estate);
        } else {
            entity.setEstate(null);
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        entity.setPassword(encodedPassword);
        return userRepository.save(entity).getId();
    }

    public void update(Long id, UserRequestDTO dto) {
        User entity = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        userMapper.toUpdate(entity, dto);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(dto.getPassword());
            entity.setPassword(encodedPassword);
        }

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        entity.setRole(role);

        if (null!=dto.getEstateId()) {
            Estate estate = estateRepository.findById(dto.getEstateId()).orElseThrow(() -> new RuntimeException("Estate not found"));
            entity.setEstate(estate);
        } else {
            entity.setEstate(null);
        }

        userRepository.save(entity);
    }

    public void delete(Long id) {
        User entity = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity.setActive(false);
        userRepository.save(entity);
    }

    public void updateLastLogin(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        userOptional.ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }

}
