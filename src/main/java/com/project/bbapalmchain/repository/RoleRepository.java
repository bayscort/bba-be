package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Role;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByActiveTrue(Sort sort);


    Optional<Role> findByName(String name);
}
