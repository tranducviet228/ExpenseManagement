package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.RoleEntity;
import com.kma.project.expensemanagement.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(ERole name);

}
