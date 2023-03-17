package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Page<CategoryEntity> findAllByNameLikeIgnoreCaseAndParentId(Pageable pageable, String search, Long parentId);

    List<CategoryEntity> findAllByNameLikeIgnoreCaseAndCreatedBy(String search, Long createdBy);


}
