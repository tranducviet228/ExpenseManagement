package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.CategoryLogoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryLogoRepository extends JpaRepository<CategoryLogoEntity, Long> {

    List<CategoryLogoEntity> getAllByIdIn(List<Long> ids);

}
