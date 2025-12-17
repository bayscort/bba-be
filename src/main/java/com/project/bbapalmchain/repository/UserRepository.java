package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByActiveTrue(Sort sort);

    List<User> findByRoleId(Long roleId);

    List<User> findByRoleIdIn(List<Long> roleIds);


}
