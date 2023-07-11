package com.eca.registration.dao;

import com.eca.registration.model.ERole;
import com.eca.registration.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByRole(ERole role);
}
